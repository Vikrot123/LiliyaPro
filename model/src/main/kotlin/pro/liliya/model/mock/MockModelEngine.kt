package pro.liliya.model.mock

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import pro.liliya.domain.api.ModelEngine
import pro.liliya.domain.models.ContextSnapshot

class MockModelEngine : ModelEngine {

    private var loaded = false

    override suspend fun loadModel(): Boolean {
        loaded = true
        return true
    }

    override suspend fun unloadModel() {
        loaded = false
    }

    override fun isModelLoaded(): Boolean {
        return loaded
    }

    override fun streamInference(
        snapshot: ContextSnapshot
    ): Flow<String> = flow {

        if (!loaded) {
            emit("Model not loaded")
            return@flow
        }

        emit("Thinking...")
        emit("I received: ${snapshot.userInput}")
        emit("LiliyaPro MVP response")
    }
}
