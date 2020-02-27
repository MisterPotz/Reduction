package com.reducetechnologies.specificationsAndRequests

/**
 * В этот класс должны быть внесены основные типы конструкций, касающихся редукторов
 * и их начинок. Все юудущие изменения должны быть отражены в спецификациях этого класса.
 */
object Specifications{
    /**
     * Какие вообще могут быть редукторы.
     * 1) [CYLINDRICAL] - обычный цилиндрический редуктор, оси параллельны
     * 2) [PLANETAR] - все сложно
     * 3) [CONE] - оси пересекаются
     */
    enum class ReducerType{CYLINDRICAL,  PLANETAR, CONE}

    /**
     * Спецификация по количеству ступеней
     */
    enum class StagesAmount(val number: Int){SINGLE(1), DOUBLE(2), TRIPLE(3)}

    /**
     * В будушем возможно расширение типа ступенй
     */
    enum class StageType{COMMON}

    /**
     * Тип отдельного колеса / сцепления в рамках отдельной ступени
     * [CYLINDRICAL] - цилиндрическое
     * [CONE] - коническое
     */
    enum class WheelType{CYLINDRICAL, CONE}//В цилиндрические колёса входят
    //прямозубые, косозубые и шевронные
    /**
     * Подтип отдельного колеса / сцепления в рамках отдельной ступени
     * [SPUR] - прямозубое колесо
     * [HELICAL] - косозубое колесо
     * [CHEVRON] - шевронное буквой V
     * [DOUBLE_CHEVRON] - колесо, разбиенное на два полушеврона - его не существует, это
     * CHEVRON с BKAN
     * [NP] - это индекс типа передачи: 1 - цилиндрическая косозубая, 2 - коническая с круглым
     * зубом, 3 - любая прямозубая
     */
    enum class WheelSubtype(val NP: Int){SPUR(3), HELICAL(1), CHEVRON(1)}
}

data class EngineRequest(val doCalculate : Boolean)

/**
 * Запрос на отдельное колесо
 */
data class WheelRequest(val wheelType: Specifications.WheelType,
                        val wheelSubtype: Specifications.WheelSubtype
)//убрал null,
//здесь так можно?

/**
 * Запрос касательно одной ступени
 * [stageType] - спецификация на отдельную ступень
 * [wheelTypes] - спецификации на каждое колесо. В дальнейшем если понадобится - вынест
 */
data class StageRequest(val stageType: Specifications.StageType = Specifications.StageType.COMMON,
                        val wheelsRequest: List<WheelRequest>)

/**
 * Запрос касательно всех ступеней
 * [stagesAmount] - количество логических ступеней
 * [stageRequests] - список спецификаций на каждую ступень. С возрастанием позиции в списке
 * растет отдаление ступени от входного вала и наоборот - чем дальше в списке ступень тем ближе она
 * к выходному валу
 */
data class StagesRequest(val stagesAmount: Specifications.StagesAmount,
                         val stageRequests: List<StageRequest>)

/**
 * Запрос касательно всего редуктора. Состоит из:
 * [reducerType] - типа редуктора
 * [stagesRequest] - характеристик по ступеням
 * [engine] - требования касательно двигателя
 */
data class ReducerCreationRequest(val reducerType: Specifications.ReducerType,
                                  val stagesRequest: StagesRequest,
                                  val engine : EngineRequest
)
