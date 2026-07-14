package pro.liliya.domain.api

import kotlinx.coroutines.flow.Flow

interface ExecutiveController {

    suspend fun processInput(
        input: String
    ): Flow<String>

    suspend fun pause()

    suspend fun resume()
}
