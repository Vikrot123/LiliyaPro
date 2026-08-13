package pro.liliya.core.runtime.capability

interface RuntimeCapabilityInfrastructure {

    fun lifecycle(): RuntimeModuleCapabilityBinder

    fun discoveryRegistry(): RuntimeCapabilityDiscoveryRegistry
}
