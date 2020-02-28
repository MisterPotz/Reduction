package com.reducetechnologies.reducerLogic

import com.reducetechnologies.interfacesAndAbstractions.Engine
import com.reducetechnologies.interfacesAndAbstractions.Shaft
import com.reducetechnologies.interfacesAndAbstractions.Stage
import com.reducetechnologies.interfacesAndAbstractions.Wheel
import com.reducetechnologies.specificationsAndRequests.*


/**
 * У фабрики редукторов должны быть классы, отображающие под-фабрики для движков и подфабрики
 * ступеней. В свою очередь, у подфабрики ступеней есть подфабрики для отдельной ступени.
 * Прикол в том, что у цилиндрических редукторов фабрики для подэлементов - свои, у планетарных - свои,
 * и так далее вниз по цепочке. Если мы закладываем такуювесьма гибкую но не очень простую архитектуру,
 * то это паттерн стратегия комбинированный с фабриками.
 *
 */
abstract class ReducerFabric(var reducerRequest: ReducerCreationRequest) {
    /**
     * Поскольку двигатель малозависим, от того, какой тип редуктора, можно просто сразу определить
     * метод создания двигателя
     */

    /**
     * Создатель ступеней уже зависит от того, какого типа редуктор, поэтому этот класс должен определяться
     * в субклассах
     */
    abstract class StagesFabric(var stagesRequest: StagesRequest) {
        abstract fun createStages(): List<Stage>

    }

    abstract class StageFabric(var stageRequest: StageRequest) {
        abstract fun createStage(): Stage
    }

    abstract class WheelFabric(var wheelRequest: WheelRequest) {
        abstract fun createWheel(): Wheel
    }

    abstract class ShaftsFabric() {
        abstract fun createShafts(): List<Shaft>
    }

    /**
     * Так как у каждого субкласса Reducer Fabric могут определяться свои фабрики для StagesFabric и
     * так далее, и причем у каждого субкласса ReducerFabric может быть множество своих субклассов
     * StagesFabric, должна быть штука, которая бы по спецификациям запроса могла бы выбирать среди
     * всех этих нужных субклассов правильную фабрику*/
    interface StagesFabricSelector {
        fun getFabric(stagesRequest: StagesRequest): StagesFabric
    }

    abstract fun createReducer(): Reducer
}