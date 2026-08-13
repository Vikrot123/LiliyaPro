package pro.liliya.core.runtime.capability

import pro.liliya.core.runtime.control.RuntimeCommand

interface MutableRuntimeCapabilityRegistry : RuntimeCapabilityRegistry {

    fun register(
        definition: RuntimeCapabilityDefinition
    )

    fun unregister(
        command: RuntimeCommand
    )
}
