package pro.liliya.core.runtime.orchestration

class DefaultRuntimeActionController(
    private val composition: RuntimeActionComposition
) : RuntimeActionController {

    override fun register() {
        composition.registerRuntimeControls()
        composition.registerRuntimeActionHandlers()
    }
}
