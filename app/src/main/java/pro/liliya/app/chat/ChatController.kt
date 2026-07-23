package pro.liliya.app.chat

import kotlinx.coroutines.flow.Flow
import pro.liliya.app.application.ConversationService

class ChatController {

    private val conversationService = ConversationService()

    suspend fun start() {
        conversationService.start()
    }

    suspend fun stop() {
        conversationService.stop()
    }

    fun welcomeMessage(): String {

        return """
LiliyaPro

Инициализация завершена.

Когнитивное ядро готово.

Введите сообщение ниже.
""".trimIndent()

    }

    suspend fun sendMessage(
        message: String
    ): Flow<String> {

        return conversationService.sendMessage(
            message
        )
    }

}
