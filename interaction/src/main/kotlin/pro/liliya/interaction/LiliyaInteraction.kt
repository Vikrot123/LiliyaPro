package pro.liliya.interaction

object LiliyaInteraction {

    private val runtime =
        LiliyaInteractionRuntimeFactory.create()

    fun start() {
        runtime.start()
    }

    fun stop() {
        runtime.stop()
    }

    fun state(): LiliyaInteractionRuntimeState {
        return runtime.state()
    }

    fun process(
        request: LiliyaInteractionRequest
    ): LiliyaInteractionResult {
        return runtime.process(request)
    }
}
