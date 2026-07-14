package pro.liliya.domain.api

import kotlinx.coroutines.flow.StateFlow
import pro.liliya.domain.models.RuntimeState
import pro.liliya.domain.models.StateTransition

interface RuntimeStateMachine {

    /**
     * Текущее состояние Runtime.
     */
    val currentState: StateFlow<RuntimeState>


    /**
     * История переходов состояния.
     */
    val history: StateFlow<List<StateTransition>>


    /**
     * Изменить состояние.
     */
    suspend fun transitionTo(state: RuntimeState)


    /**
     * Получить текущее состояние.
     */
    fun state(): RuntimeState
}
