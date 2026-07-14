package pro.liliya.domain.api

import kotlinx.coroutines.flow.Flow
import pro.liliya.domain.models.ContextSnapshot

interface ModelEngine {

    suspend fun loadModel(): Boolean

    suspend fun unloadModel()

    fun isModelLoaded(): Boolean

    fun streamInference(
        snapshot: ContextSnapshot
    ): Flow<String>
}
