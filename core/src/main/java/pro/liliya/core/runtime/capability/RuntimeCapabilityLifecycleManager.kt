package pro.liliya.core.runtime.capability

import pro.liliya.core.runtime.control.RuntimeCommand

interface RuntimeCapabilityLifecycleManager {

    fun register(
        capability: RuntimeManagedCapability
    )

    fun activate(
        command: RuntimeCommand
    )

    fun disable(
        command: RuntimeCommand
    )

    fun remove(
        command: RuntimeCommand
    )

    fun find(
        command: RuntimeCommand
    ): RuntimeManagedCapability?

    fun reset()
}
