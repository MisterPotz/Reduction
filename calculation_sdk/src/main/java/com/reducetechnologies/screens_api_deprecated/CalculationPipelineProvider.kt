package com.reducetechnologies.screens_api_deprecated

import com.reducetechnologies.command_stacks_deprecated.ProtoScreen

/**
 * Задача данного класса - отображать в своей структуре дерево экранов и предоставлять следующий
 * при запросе. Пока планируется, что здесь линейная последовательность жкранов, а всякие изменния
 * будут внедряться путем внесения на эти экраны дополнительной информации с помощью другой (не жтой
 * сущности). Но если понадобится делать ветвь решений для разных событий, чтобы карточки ветвились -
 * тогда это должно быть реализовано здесь. Причем здесь это будет реализовано через Inner Mutable
 * State, который будет меняться сущностью, которая шарит за текущее состояние расчетов.
 */
class CalculationPipelineProvider {
    /**
     * Defining protoscreen initial sequence
     */
    val protoScreenList: List<ProtoScreen> = listOf(

    )
}
