package pro.liliya.core.runtime.capability

interface RuntimeCapabilityInfrastructure {

    fun lifecycleManager(): RuntimeCapabilityLifecycleManager

    fun lifecycle(): RuntimeModuleCapabilityBinder

    fun discoveryRegistry(): RuntimeCapabilityDiscoveryRegistry
}
