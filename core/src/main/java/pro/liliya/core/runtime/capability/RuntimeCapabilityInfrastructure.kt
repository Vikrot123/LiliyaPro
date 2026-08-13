package pro.liliya.core.runtime.capability

import pro.liliya.core.runtime.module.RuntimeModuleCapabilityLifecycle

interface RuntimeCapabilityInfrastructure {

    fun lifecycleManager(): RuntimeCapabilityLifecycleManager

    fun lifecycle(): RuntimeModuleCapabilityBinder

    fun discoveryRegistry(): RuntimeCapabilityDiscoveryRegistry

    fun moduleCapabilityLifecycle(): RuntimeModuleCapabilityLifecycle
}
