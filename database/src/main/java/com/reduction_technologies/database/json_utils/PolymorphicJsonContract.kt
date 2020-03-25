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

    abstract class Contract<T>{
        /**
         * In contracts Type enum must present - it is used
         * as key in typeRegistry
         */
        enum class Type(val string: String)

        val fieldName : String = "type"
        abstract val typeRegistry: Map<String, Class<out T>>
    }
}

abstract class PolymorphicDeserializer<T>(
    val contract : PolymorphicParent.Contract<T>,
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

abstract class PolymorphicSerializer<T : PolymorphicParent>(
    val contract : PolymorphicParent.Contract<T>,
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