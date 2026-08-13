package pro.liliya.core.runtime.capability

data class RuntimeManagedCapability(
    val definition: RuntimeCapabilityDefinition,
    var state: RuntimeCapabilityState = RuntimeCapabilityState.REGISTERED
)
