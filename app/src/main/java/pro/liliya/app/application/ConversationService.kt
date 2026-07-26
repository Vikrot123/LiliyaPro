package pro.liliya.app.application


import kotlinx.coroutines.flow.Flow
import pro.liliya.runtime.LiliyaRuntime



class ConversationService(
    private val runtime: LiliyaRuntime
) {



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
