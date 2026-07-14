package pro.liliya.core.orchestration

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import pro.liliya.domain.api.ExecutiveController
import pro.liliya.domain.api.ModelEngine
import pro.liliya.domain.models.ContextSnapshot

class ExecutiveControllerImpl(
    private val modelEngine: ModelEngine
) : ExecutiveController {

    private val mutex = Mutex()

    private var paused = false

    override suspend fun processInput(
        input: String
    ): Flow<String> {

        return mutex.withLock {

            if (paused) {
                return@withLock flow {
                    emit("System paused")
                }
            }

            val snapshot = ContextSnapshot(
                userInput = input,
                timestamp = System.currentTimeMillis(),
                stateId = "initial"
            )

            modelEngine.streamInference(snapshot)
        }
    }

    override suspend fun pause() {
        mutex.withLock {
            paused = true
        }
    }

    override suspend fun resume() {
        mutex.withLock {
            paused = false
        }
    }
}
