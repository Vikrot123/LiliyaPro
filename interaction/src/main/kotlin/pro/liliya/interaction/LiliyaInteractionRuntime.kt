package pro.liliya.interaction

class LiliyaInteractionRuntime(
    private val gateway: LiliyaInteractionGateway,
    private val lifecycle: LiliyaInteractionLifecyclePort
) {

    fun start() {
        lifecycle.start()
    }

    fun stop() {
        lifecycle.stop()
    }

    fun state(): LiliyaInteractionRuntimeState {
        return lifecycle.state()
    }

    fun process(
        request: LiliyaInteractionRequest
    ): LiliyaInteractionResult {
        return gateway.process(request)
    }
}
