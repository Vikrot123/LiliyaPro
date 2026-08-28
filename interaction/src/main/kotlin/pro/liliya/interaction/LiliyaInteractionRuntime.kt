package pro.liliya.interaction

internal class LiliyaInteractionRuntime(
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
        val currentState =
            lifecycle.state()

        if (currentState != LiliyaInteractionRuntimeState.RUNNING) {
            throw LiliyaInteractionRuntimeException(
                "Interaction processing requires RUNNING runtime; current state: $currentState"
            )
        }

        return gateway.process(request)
    }
}
