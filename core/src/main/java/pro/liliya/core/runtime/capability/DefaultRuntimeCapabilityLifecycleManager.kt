package pro.liliya.core.runtime.capability

import pro.liliya.core.runtime.control.RuntimeCommand

class DefaultRuntimeCapabilityLifecycleManager :
    RuntimeCapabilityLifecycleManager {

    private val capabilities =
        mutableMapOf<RuntimeCommand, RuntimeManagedCapability>()

    override fun register(
        capability: RuntimeManagedCapability
    ) {
        capabilities[capability.definition.command] = capability
    }

    override fun activate(
        command: RuntimeCommand
    ) {
        capabilities[command]?.state =
            RuntimeCapabilityState.ACTIVE
    }

    override fun disable(
        command: RuntimeCommand
    ) {
        capabilities[command]?.state =
            RuntimeCapabilityState.DISABLED
    }

    override fun remove(
        command: RuntimeCommand
    ) {
        capabilities[command]?.state =
            RuntimeCapabilityState.REMOVED
    }

    override fun find(
        command: RuntimeCommand
    ): RuntimeManagedCapability? {
        return capabilities[command]
    }

    override fun reset() {
        capabilities.clear()
    }
}
