package pro.liliya.app.application

import kotlinx.coroutines.flow.Flow

class ConversationService {

    private val runtime = AppContainer.runtime

    suspend fun start() {
        runtime.start()
    }

    suspend fun stop() {
        runtime.stop()
    }

    suspend fun sendMessage(
        message: String
    ): Flow<String> {

        return runtime.process(message)
    }
}
