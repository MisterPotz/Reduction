package com.reducetechnologies.command_stacks

import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

/**
 * Кдасс для хранения прото-экранов. Фиговина, которую может строить бэк, и которую фронт должен
 * отображать на экранчике. Прото скрины фигачатся в некий стек, инкапсулированный в наследник
 * интерфейса общения бэка с фронтом.
 */
abstract class ProtoScreen {
    /**
     * Могут быть несколько типов протоэкранов.
     * [OUTPUT_SCREEN] - экран просто выводит информацию, у него нет элементов вводв
     * [INPUT_SCREEN] - экран может запрашивать информацию, которую бэк должен проверять в обязательном
     * порядке
     */
    enum class ProtoScreenType{
        OUTPUT_SCREEN,
        INPUT_SCREEN
    }
    abstract val protoScreenType : ProtoScreenType
    // Заголовок карточки
    abstract val titlePane: String

    // Идентификатор прото-экрана для опознавания бэком
    abstract val protoScreenId: Int

    // Список полей, присущих этому прото-экрану
    abstract var fields: List<Field>
}

/**
 * [fieldId] Хранит номер ID, присвоенный бэку этому полю. После того как поле отработало, клиент возвращает
 * поле бэку, бэк сравнивает номер айди поля с ранее присвоенным и понимает, то это поле или нет
 */
abstract class Field(
    val fieldId: Int,
    val type: FieldType
) {
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
    fieldId: Int
) : Field(fieldId = fieldId, type = FieldType.SIMPLE_TEXT)

/**
 * Класс для зранения информации о картинке. Изображение передается через закодированную строку в
 * формате Base64.
 */
class SimpleImageField(
    val encodedImage: String,
    fieldId: Int
) : Field(fieldId = fieldId, type = FieldType.SIMPLE_IMAGE)

/**
 * Класс, хранящий информацию по подсказке. Может иметь: текст, картинку.
 * Если ни одна из переменных не будет определена - будет ошибка, т.к. если используется подсказка,
 * она не может быть пустой.
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
abstract class HintField(val hint: Hint, fieldId: Int, fieldType: Field.FieldType) :
    Field(fieldId = fieldId, type = fieldType) {
}

class MathTextField(val textWithMaths: String, fieldId: Int, fieldType: Field.FieldType) :
    Field(fieldId = fieldId, type = fieldType)

/**
 * [title] - название поля ввода
 * [hintText] - информация об ожидаемой информации (допустимый диапазон, тип переменной)
 */
class InputTextField(
    val title: String,
    val hintText: String,
    fieldId: Int
) :
    Field(fieldId = fieldId, type = FieldType.INPUT_TEXT) {
    var error: String? = null

    constructor(
        title: String,
        hintText: String,
        error: String,
        fieldId: Int
    ) : this(title, hintText, fieldId) {
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
                input = InputTextField(title, hintText, error, fieldId)
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
    fieldId: Int
) :
    Field(fieldId = fieldId, type = FieldType.INPUT_IMAGE)

/**
 * [options] - список с вариантами, где нужно выбрать один из них
 * [title] - название поля ввода
 * [hintText] - информация об ожидаемой информации (допустимый диапазон, тип переменной)
 * В этом классе пока нет поля error, так как лист фиксирован и любой из вариантов должен быть верным
 */
class InputListField(
    val options: List<String>,
    val title: String,
    val hintText: String,
    fieldId: Int
) :
    Field(fieldId = fieldId, type = FieldType.INPUT_COMBO_BOX)



