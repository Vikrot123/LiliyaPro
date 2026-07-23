package pro.liliya.app.application

import pro.liliya.runtime.LiliyaRuntime
import pro.liliya.app.chat.ChatController

/**
 * Центральный контейнер приложения.
 *
 * Единственная точка создания Runtime
 * и Application-level сервисов.
 */
object AppContainer {

    val runtime: LiliyaRuntime by lazy {
        LiliyaRuntime()
    }


    val chatController: ChatController by lazy {
        ChatController()
    }

}
