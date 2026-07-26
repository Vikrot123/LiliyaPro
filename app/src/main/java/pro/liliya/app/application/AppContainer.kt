package pro.liliya.app.application


import pro.liliya.app.chat.ChatController
import pro.liliya.runtime.LiliyaRuntime



object AppContainer {



    val runtime: LiliyaRuntime by lazy {

        LiliyaRuntime()

    }



    val conversationService: ConversationService by lazy {

        ConversationService(
            runtime
        )

    }



    val chatController: ChatController by lazy {

        ChatController(
            conversationService
        )

    }



    val runtimeStatus: RuntimeStatusService by lazy {

        RuntimeStatusService()

    }

}
