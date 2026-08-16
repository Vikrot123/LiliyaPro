package pro.liliya.core.runtime.orchestration

class DefaultRuntimeServiceController(
    private val composition: RuntimeServiceComposition
) : RuntimeServiceController {

    override fun start() {
        composition.startRuntimeServices()
    }

    override fun stop() {
        composition.stopRuntimeServices()
    }
}
