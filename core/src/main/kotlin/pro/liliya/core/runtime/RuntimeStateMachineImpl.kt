package pro.liliya.core.runtime

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import pro.liliya.domain.api.RuntimeStateMachine
import pro.liliya.domain.models.RuntimeState
import pro.liliya.domain.models.StateTransition

class RuntimeStateMachineImpl : RuntimeStateMachine {

    private val mutex = Mutex()

    private val _state =
        MutableStateFlow(RuntimeState.BOOTING)

    private val _history =
        MutableStateFlow<List<StateTransition>>(emptyList())

    override val currentState: StateFlow<RuntimeState>
        get() = _state.asStateFlow()

    override val history: StateFlow<List<StateTransition>>
        get() = _history.asStateFlow()


    override suspend fun transitionTo(
        state: RuntimeState
    ) {
        mutex.withLock {

            val previousState = _state.value

            if (previousState != state) {

                val transition = StateTransition(
                    from = previousState,
                    to = state
                )

                _history.value =
                    _history.value + transition

                _state.value = state
            }
        }
    }


    override fun state(): RuntimeState {
        return _state.value
    }
}
