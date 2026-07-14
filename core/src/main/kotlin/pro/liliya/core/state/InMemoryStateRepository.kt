package pro.liliya.core.state

import pro.liliya.domain.api.StateRepository
import pro.liliya.domain.models.StateVector

class InMemoryStateRepository : StateRepository {

    private var currentState = StateVector()

    override suspend fun saveState(
        state: StateVector
    ) {
        currentState = state
    }

    override suspend fun getState(): StateVector {
        return currentState
    }

    override suspend fun clear() {
        currentState = StateVector()
    }
}
