package com.reducetechnologies.command_stacks

import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

/**
 * Отображает логику, которая должна быть связанной с этим протоскрином.
 * [MUST_BE_FILLED] - экран должен быть сначал заполнен на беке какими-то значениями прежде чем
 * возвращаться пользователю
 * [HAS_INPUT] - после показа юзеру, экран возвращается с заполненными значениями, которые
 * должны быть проверены на беке.
 * Если значения лажовые - перепоказать пользователю экран с указанием ошибки
 * Если значения норм - можно переходить к следующему экрану если таковой имеется.
 * Этим тегом можно помечать и поля.
 */
enum class RequiredProcessing {
    MUST_BE_FILLED, HAS_INPUT
}

interface Taggable {
    val tags: List<RequiredProcessing>
}

/**
 * Кдасс для хранения прото-экранов. Фиговина, которую может строить бэк, и которую фронт должен
 * отображать на экранчике. Прото скрины фигачатся в некий стек, инкапсулированный в наследник
 * интерфейса общения бэка с фронтом.
 */
abstract class ProtoScreen(
    // Заголовок карточки
    val titlePane: String,
    // Идентификатор прото-экрана для опознавания бэком
    val protoScreenId: Int,
    // Список полей, присущих этому прото-экрану
    val fields: List<Field>
) : Taggable {
    // Список меток для этой карточки, заполняется при инициализации
    // Может сделать что-то типа билдера протоскрина?
    // Субклассы протоскрина должны предоставить функцию заполнения тагов по ProtoScreen
    override val tags: List<RequiredProcessing> = Companion.tags(fields)

    // Checks if proto screen has input fields and therefore has to be validated
    fun hasInput(): Boolean {
        /*return fields.fold(false) { left, rigth ->
            return left || when (rigth.type) {
                Field.FieldType.INPUT_TEXT,
                Field.FieldType.INPUT_COMBO_BOX,
                Field.FieldType.INPUT_IMAGE -> true
                else -> false
            }
        }*/
        return RequiredProcessing.HAS_INPUT in tags
    }

    // Cheks if has validatable fields
    fun hasInlatableFields(): Boolean {
        return RequiredProcessing.MUST_BE_FILLED in tags
    }

    // Проаналазировать список полей и заполнить на их основе список меток, присущих данному Protoscreen
    // Фабричный метод для тегов
    abstract fun fillTags()

    companion object {
        fun tags(fields: List<Field>): List<RequiredProcessing> {
            val set: HashSet<RequiredProcessing> = HashSet()
            for (field in fields) {
                for (tag in field.tags) {
                    set.add(tag)
                }
            }
            return set.toList()
        }
    }
}

/**
 * [fieldId] Хранит номер ID, присвоенный бэку этому полю. После того как поле отработало, клиент возвращает
 * поле бэку, бэк сравнивает номер айди поля с ранее присвоенным и понимает, то это поле или нет
 */
abstract class Field(
    val fieldId: Int,
    val type: FieldType,
    final override val tags: List<RequiredProcessing>
) : Taggable {
    /**
     * [SIMPLE_TEXT] - обычный описательный текст
     * [SIMPLE_IMAGE] - обычная картинка
     * [HINT] - подсказочное поле
     * [MATH_TEXT] - текст, в котором встречаются математические формулы,
     * которые следует хорошо и красиво отобразить
     * [INPUT_TEXT] - поле, куда юзер что-то должен вводить
     * [INPUT_IMAGE] - поле, где выбирается картинка (картинка схемы например)
     * [INPUT_COMBO_BOX] - поле, где есть раскрывающийся список, откуда нужно выбрать нужное поле
     */
    enum class FieldType {
        SIMPLE_TEXT, SIMPLE_IMAGE, HINT, MATH_TEXT, INPUT_TEXT, INPUT_IMAGE, INPUT_COMBO_BOX
    }
}


/**
 * [text] - текст, который должно отображать поле
 * [fieldId] - уникальный идентификатор
 * Должен предоставлять просто логику получения текста
 */
class SimpleTextField(
    val text: String,
    fieldId: Int,
    tags: List<RequiredProcessing>
) : Field(fieldId = fieldId, type = FieldType.SIMPLE_TEXT, tags = tags) {

}

/**
 * Класс для зранения информации о картинке. Изображение передается через закодированную строку в
 * формате Base64.
 */
class SimpleImageField(
    val encodedImage: String,
    fieldId: Int,
    tags: List<RequiredProcessing>
) : Field(fieldId = fieldId, type = FieldType.SIMPLE_IMAGE, tags = tags)

/**
 * Класс, хранящий информацию по подсказке. Может иметь: текст, картинку.
 * Если ни одна из переменных не будет определена - будет ошибка, т.к. если используется подсказка,
 * она не может быть пустой.
 * Но пока непонятно, понадобится ли он на самом деле.
 */
data class Hint(val name: String, var mainText: String? = null, var mainImage: String? = null) {
    init {
        if (mainText == null && mainImage == null) {
            logger.error { "Cannot create empty hint" }
            throw IllegalStateException("Hint is empty: $name")
        }
    }
}

/**
 * Поле с подсказкой. Наличие подсказки означает какой-либо элемент (кнопка,
 * раскрывающееся полотно, - как она отображается описывает фронт), который ссылает на термин, или
 * кожффициент, к которому нужны какие-либо пояснения / определение и т.д.
 * Все эти пояснения и т.д.должны храниться в SQLite  БД, где каждая строка - относится к какому-то
 * термину, а в столбцах таблички на той же строке - соответствующие пояснения, определения и т.д.
 */
abstract class HintField(
    val hint: Hint, fieldId: Int,
    fieldType: Field.FieldType,
    tags: List<RequiredProcessing>
) :
    Field(fieldId = fieldId, type = fieldType, tags = tags) {
}

class MathTextField(
    val textWithMaths: String, fieldId: Int,
    fieldType: Field.FieldType,
    tags: List<RequiredProcessing>
) :
    Field(fieldId = fieldId, type = fieldType, tags = tags)

/**
 * [title] - название поля ввода
 * [hintText] - информация об ожидаемой информации (допустимый диапазон, тип переменной)
 */
class InputTextField(
    val title: String,
    val hintText: String,
    fieldId: Int,
    tags: List<RequiredProcessing>
) :
    Field(fieldId = fieldId, type = FieldType.INPUT_TEXT, tags = tags) {
    var error: String? = null
    var inputText: String? = null

    constructor(
        title: String,
        hintText: String,
        error: String,
        fieldId: Int,
        tags: List<RequiredProcessing>
    ) : this(title, hintText, fieldId, tags) {
        this.error = error
    }

    companion object {
        /**
         *  Должна быть использованана бэке, когда он поймет, что пользователь ввел
         *  данные с ошибкой, тогда он копирует пржний InputTextField и передает в новый также
         *  сообщение об ошибке
         */
        fun copyWithError(copied: InputTextField, error: String): InputTextField {
            var input: InputTextField? = null
            copied.apply {
                input = InputTextField(title, hintText, error, fieldId, tags)
            }
            return input!!
        }
    }
}

/**
 * [encodedImages] - массив картинок, из которых нужно выбрать одну
 * [title] - название поля ввода
 * [hintText] - информация об ожидаемой информации (допустимый диапазон, тип переменной)
 * поля error нет, так как среди предлагаемых картинок ллюая должна быть верной, пользователь не
 * должен ошибиться
 */
class InputPicturesField(
    val encodedImages: List<String>,
    val title: String,
    val hintText: String,
    fieldId: Int,
    tags: List<RequiredProcessing>
) :
    Field(fieldId = fieldId, type = FieldType.INPUT_IMAGE, tags = tags) {
    var chosenPictureOption: Int? = null
}

/**
 * [options] - список с вариантами, где нужно выбрать один из них
 * [title] - название поля ввода
 * [hintText] - информация об ожидаемой информации (допустимый диапазон, тип переменной)
 * В этом классе пока нет поля error, так как лист фиксирован и любой из вариантов должен быть верным
 */
// TODO сделать для input fields автоматическую простановку тага "принимает ввод"
class InputListField(
    val options: List<String>,
    val title: String,
    val hintText: String,
    fieldId: Int,
    tags: List<RequiredProcessing>
) :
    Field(fieldId = fieldId, type = FieldType.INPUT_COMBO_BOX, tags = tags) {
    var chosenListOption: Int? = null
}



