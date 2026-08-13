package pro.liliya.core.runtime.capability

import pro.liliya.core.runtime.control.RuntimeCommand

class RuntimeCapabilityManager(
    private val registry: MutableRuntimeCapabilityRegistry =
        DefaultMutableRuntimeCapabilityRegistry()
) {

    fun register(
        definition: RuntimeCapabilityDefinition
    ) {
        registry.register(definition)
    }

    fun unregister(
        command: RuntimeCommand
    ) {
        registry.unregister(command)
    }

    fun find(
        command: RuntimeCommand
    ): RuntimeCapabilityDefinition? {
        return registry.find(command)
    }

    fun registry(): RuntimeCapabilityRegistry {
        return registry
    }
}
