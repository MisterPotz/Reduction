package com.reducetechnologies.screens_api_deprecated

import com.reducetechnologies.command_stacks_deprecated.ProtoScreen

/**
 * Что должен делать класс:
 * 1) Предоставлять экраны
 * Какая логика за ним стоит (не обязательно в нем, но на этом уровне иерархии):
 * 1) Должен уметь пополнять экраны - то есть у него где-то есть источник жэтих экранов
 * 2) Должен предоставлять логику обработки запроса. Если в запросе есть экран - и в нем
 * есть введенные данные (допустим), этот класс (или его делегат) должен понимать,
 * правильны ли ээти введенные данные, если нет - встроить в очередь жкран, но с указанием ошибок,
 * если да - брать новый экран из очереди. То есть очередь - нормальная очеред (/ сборник экранов,
 * независимых от поведения) должен быть в другом классе, отвечающем за жто.
 * То есть зависит от:
 *  ---- Класс, клторый знает полную очередь экранов
 *  ---- Класс, который знает как понять, правильный запрос был или нет
 *  не важно где, но указанные логики не должны быть прямо здесь.
 *  Здесь - непосредственно логика выдачи ответов
 *  Если неправильный ввод - ввернуть экран с информацией
 *  Если правильный - выдать следующий
 */
class ScreenStackApiImpl
constructor(private val protoScreenManager: ProtoScreenManager) : ScreenStackApi {
    /**
     * Не должно быть такого, что сразу несколько потоков будут запрашивать или пополнять
     * данный массив, доступ только из одного потока
     */

    override fun getProtoScreen(request: ProtoScreenRequest): ProtoScreenResponse {
        if (!protoScreenManager.hasMore()) {
            return ErrorResponse("Screens are ended")
        }
        return when (request) {
            is InitialRequest -> onInitialRequest()
            is CommonRequest -> onCommonRequest(request)
        }
    }

    private fun onInitialRequest(): ProtoScreenResponse {
        val response = protoScreenManager.getNext(null, ProtoScreenManager.State.INITIAL)
        return CommonResponse(response)
}

    private fun onCommonRequest(request: CommonRequest): ProtoScreenResponse {
        val response = protoScreenManager.getNext(protoScreen = request.lastScreen)
        val isLast = protoScreenManager.isLast()
        return CommonResponse(response)
    }
}

// Принимает прото скрин с данными ввода и сообщает, был он верным или нет
// На основании результатов внутри субкласса этого интерфейса могут быть внутренние сайд-эффекты
interface ProtoScreenValidator {
    fun processAndValidateRequest(protoScreen: ProtoScreen): Boolean
}

/**
 * Возможно понадубится некая сущность в конструкторе, которая будет знать, когда ее пополняют
 * и которая сможет при собственном пополнении пополнять здешний массив прото экранов (механизм
 * наблюдателя)
 * Скоее всего да, потому что когда бэк закончит рассчет, там будут свои цифры, с их учетом
 * он с помощью механизма билдера сможет наполнить поля нужной инофй, затем добавит это в
 * наблюдаемый элемент этого класса или в сам этот класс, и потом массив карточек пополнится.
 */
abstract class ProtoScreenManager : ProtoScreenValidator {
    enum class State { INITIAL }

    // Задается вручную
    private val screenArray: Array<ProtoScreen> = arrayOf()

    private var currentCounter: Int = 0

    /**
     * Only one of the functions must be called at once
     * increment inner counter
     */
    abstract protected fun processLast(type: State): Boolean

    abstract protected fun processLast(protoScreen: ProtoScreen): Boolean

    abstract fun getNext(protoScreen: ProtoScreen?, type: State? = null): ProtoScreen

    abstract fun isLast(): Boolean

    fun hasMore(): Boolean {
        return currentCounter < screenArray.size
    }
}