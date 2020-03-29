package com.reduction_technologies.database.json_utils

import com.google.gson.*
import java.lang.reflect.Type

abstract class PolymorphicParent {
    /**
     * Обязательно должен быть, если хотим полиморфность джисонов
     * Так как тип класса должен быть записан в джисон, чтобы можно
     * было десериализовать его обратно
     */
    var type: String = "null"

    abstract class Contract<T> {
        /**
         * In contracts Type enum must present - it is used
         * as key in typeRegistry
         */
        enum class Type(val string: String)

        val fieldName: String = "type"
        abstract val typeRegistry: Map<String, Class<out T>>
        abstract val abstractClass: Class<T>
    }
}

class PolymorphicDeserializer<T>(
    val contract: PolymorphicParent.Contract<T>,
    val gson: Gson
) :
    JsonDeserializer<T> {
    @Throws(JsonParseException::class)

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): T {
        val jobj = json.asJsonObject
        val elementType = jobj.get(contract.fieldName).asString
        val type = contract.typeRegistry[elementType]
        return gson.fromJson<T>(json, type)
    }
}

class PolymorphicSerializer<T : PolymorphicParent>(
    val contract: PolymorphicParent.Contract<T>,
    val gson: Gson
) : JsonSerializer<T> {

    override fun serialize(
        src: T?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        val type = contract.typeRegistry[src!!.type]
        return gson.toJsonTree(src, type)
    }
}

/**
 * Declares a class that has polymorpphic members.
 * [prepareGson] return fully prepared gson for parsing target class
 * [register] add serializers and deserializers for a polymorphic member via its polymorphicmanager
 */
interface GsonRegister {
    fun <T : PolymorphicParent> GsonBuilder.register(manager: PolymorphicGsonManager<T>) : GsonBuilder{
        manager.gsonRegister(this)
        return this
    }

    fun prepareGson() : Gson
}

abstract class PolymorphicGsonManager<T : PolymorphicParent>(
    val contract: PolymorphicParent.Contract<T>
) {
    private val des: PolymorphicDeserializer<T> = PolymorphicDeserializer(contract, plainGson)
    private val ser: PolymorphicSerializer<T> = PolymorphicSerializer(contract, plainGson)

    // Returns set up gson if requested
    val gson: Gson by lazy {
        GsonBuilder().let {
            gsonRegister(it)
                .create()
        }
    }

    fun gsonRegister(builder: GsonBuilder): GsonBuilder {
        return builder.apply {
            registerTypeAdapter(contract.abstractClass, ser)
            registerTypeAdapter(contract.abstractClass, des)
        }
    }

    companion object {
        /**
         * For reusability of gson
         * Question - is companion different for different T's?
         */
        protected val plainGson = GsonBuilder().create()
    }
}