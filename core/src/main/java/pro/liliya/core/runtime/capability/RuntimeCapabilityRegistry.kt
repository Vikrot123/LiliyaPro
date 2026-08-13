package pro.liliya.core.runtime.capability

import pro.liliya.core.runtime.control.RuntimeCommand

interface RuntimeCapabilityRegistry {

    fun find(
        command: RuntimeCommand
    ): RuntimeCapabilityDefinition?
}
