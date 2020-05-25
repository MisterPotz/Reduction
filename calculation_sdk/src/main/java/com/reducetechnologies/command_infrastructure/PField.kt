package com.reducetechnologies.command_infrastructure

import com.google.gson.*
import java.lang.reflect.Type

data class PField(
    val pFieldType: PFieldType,
    val typeSpecificData: PTypeSpecific,
    val fieldId: Int
) {
    companion object {
        private val simpleGson = GsonBuilder().create()
        // reads inner pfield structures
        val gson = GsonBuilder().apply {
            registerTypeAdapter(PictureSpec::class.java, object : JsonSerializer<PictureSpec> {
                override fun serialize(
                    src: PictureSpec?,
                    typeOfSrc: Type?,
                    context: JsonSerializationContext?
                ): JsonElement {
                    if (src == null) { return JsonObject() }
                    val root = JsonObject()
                    root.addProperty("pictureSourceType", src.pictureSourceType.name)
                    when (src.pictureSourceType) {
                        PictureSourceType.PATH ->
                            root.add("source", simpleGson.toJsonTree(src.source as com.reducetechnologies.command_infrastructure.PictureStringPath))
                        PictureSourceType.TABLE_ID ->
                            root.add("source", simpleGson.toJsonTree(src.source as PictureDataTable))
                    }
                    return root
                }
            })
            registerTypeAdapter(PictureSpec::class.java, object : JsonDeserializer<PictureSpec> {
                override fun deserialize(
                    json: JsonElement?,
                    typeOfT: Type?,
                    context: JsonDeserializationContext?
                ): PictureSpec {
                    val jsonObject = json as JsonObject
                    val type = jsonObject.get("pictureSourceType").asString
                    val fieldType = PictureSourceType.valueOf(type)
                    val sourceString = jsonObject.getAsJsonObject("source")
                    val source = when (fieldType) {
                        PictureSourceType.PATH ->
                            simpleGson.fromJson(sourceString, PictureStringPath::class.java)
                        PictureSourceType.TABLE_ID ->
                            simpleGson.fromJson(sourceString, PictureDataTable::class.java)
                    }
                    return PictureSpec(fieldType, source)
                }
            })
        }.create()

        val pFieldTypeString = "pFieldType"
        val typeSpecificDataString = "typeSpecificData"
        val fieldIdString = "fieldId"

        // reads whole pfield structure
        fun makeGson(): Gson {
            val gsonBuilder = GsonBuilder()
            gsonBuilder.apply {
                registerTypeAdapter(PField::class.java, object : JsonSerializer<PField> {
                    override fun serialize(
                        src: PField?,
                        typeOfSrc: Type?,
                        context: JsonSerializationContext?
                    ): JsonElement {
                        if (src == null) {
                            return JsonObject()
                        }
                        val jsonElement = JsonObject()
                        jsonElement.addProperty("pFieldType", src.pFieldType.name)
                        jsonElement.addJsonTree(src.typeSpecificData, src.pFieldType)
                        jsonElement.addProperty("fieldId", src.fieldId)
                        return jsonElement
                    }
                })
                registerTypeAdapter(PField::class.java, object : JsonDeserializer<PField> {
                    override fun deserialize(
                        json: JsonElement?,
                        typeOfT: Type?,
                        context: JsonDeserializationContext?
                    ): PField {
                        val jsonObject = json as JsonObject
                        val type = jsonObject.get(pFieldTypeString).asString
                        val fieldType = PFieldType.valueOf(type)
                        val specific = (jsonObject.get(typeSpecificDataString) as JsonObject).toObject(fieldType)
                        val fieldId = jsonObject.get(fieldIdString).asInt
                        return PField(fieldType, specific, fieldId)
                    }
                })
            }
            return gsonBuilder.create()
        }

        fun JsonObject.toObject(fieldType: PFieldType): PTypeSpecific {
            return when (fieldType) {
                PFieldType.INPUT_LIST ->
                    asSpecific<InputListSpec>()
                PFieldType.INPUT_TEXT ->
                    asSpecific<InputTextSpec>()
                PFieldType.INPUT_PICTURE ->
                    asSpecific<InputPictureSpec>()
                PFieldType.MATH_TEXT ->
                    asSpecific<MathTextSpec>()
                PFieldType.TEXT ->
                    asSpecific<TextSpec>()
                PFieldType.PICTURE ->
                    asSpecific<PictureSpec>()
                PFieldType.LINK ->
                    asSpecific<LinkSpec>()
            }
        }

        fun JsonObject.addJsonTree(typeSpecificData: PTypeSpecific, fieldType: PFieldType) {
            val dataName = "typeSpecificData"
            when (fieldType) {
                PFieldType.INPUT_LIST ->
                    add(dataName, typeSpecificData.toJsonTreee<InputListSpec>())
                PFieldType.INPUT_TEXT ->
                    add(dataName, typeSpecificData.toJsonTreee<InputTextSpec>())
                PFieldType.INPUT_PICTURE ->
                    add(dataName, typeSpecificData.toJsonTreee<InputPictureSpec>())
                PFieldType.MATH_TEXT ->
                    add(dataName, typeSpecificData.toJsonTreee<MathTextSpec>())
                PFieldType.TEXT ->
                    add(dataName, typeSpecificData.toJsonTreee<TextSpec>())
                PFieldType.PICTURE ->
                    add(dataName, typeSpecificData.toJsonTreee<PictureSpec>())
                PFieldType.LINK ->
                    add(dataName, typeSpecificData.toJsonTreee<LinkSpec>())
            }
        }

        inline fun <reified R : PTypeSpecific> PTypeSpecific.toJsonTreee(): JsonElement {
            return gson.toJsonTree(this as R)
        }

        inline fun <reified R : PTypeSpecific> JsonObject.asSpecific(): R {
            return gson.fromJson<R>(this, R::class.java)
        }
    }
}