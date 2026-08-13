package pro.liliya.core.runtime.capability

import pro.liliya.core.runtime.control.RuntimeCommand

class DefaultMutableRuntimeCapabilityRegistry :
    MutableRuntimeCapabilityRegistry {

    private val definitions =
        mutableMapOf<RuntimeCommand, RuntimeCapabilityDefinition>()

    override fun register(
        definition: RuntimeCapabilityDefinition
    ) {
        definitions[definition.command] = definition
    }

    override fun unregister(
        command: RuntimeCommand
    ) {
        definitions.remove(command)
    }

    override fun find(
        command: RuntimeCommand
    ): RuntimeCapabilityDefinition? {
        return definitions[command]
    }
}
