package com.reducetechnologies.screens_api

import com.reducetechnologies.command_stacks.ProtoScreen

/**
 * Предоставляет АПИ для общения фронта с бэком для передачи последним первому необходимых
 * прототипов экранов, которые фронт может отображать у себя.
 */
interface ScreenStackApi {
    fun getProtoScreen(request: ProtoScreenRequest): ProtoScreenResponse
}

/**
 * У бэка можно запрашивать следующий экран, который нужно отрисовать.
 * Запросы должны быть наследованы от ProtoScreenRequest.
 * Запросы могут быть двух типов:
 * 1) Начальный запрос - у фронта еще нет ничего, что бы он мог дать бэку.
 * 2) Обычный запрос - используется при остальных ситуациях.
 */
sealed class ProtoScreenRequest

object InitialRequest : ProtoScreenRequest()

data class CommonRequest(val lastScreen: ProtoScreen) : ProtoScreenRequest()

/**
 * Классы, которыми может отвечать бэк
 * 1) Если была ошкибка - ответить классом-ошибкой с указанием причины
 * 2) Если все ок - завернуть прото-экран в ответ и отослать
 */
sealed class ProtoScreenResponse

data class ErrorResponse(val msg: String) : ProtoScreenResponse()

data class CommonResponse(val protoScreen: ProtoScreen) : ProtoScreenResponse()