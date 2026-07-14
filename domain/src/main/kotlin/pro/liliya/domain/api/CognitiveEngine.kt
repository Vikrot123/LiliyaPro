package pro.liliya.domain.api

import kotlinx.coroutines.flow.Flow

interface CognitiveEngine {

    suspend fun process(
        input: String
    ): Flow<String>

}
