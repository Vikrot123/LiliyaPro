package pro.liliya.domain.api

import pro.liliya.domain.models.StateVector

interface StateRepository {

    suspend fun saveState(
        state: StateVector
    )

    suspend fun getState(): StateVector

    suspend fun clear()
}
