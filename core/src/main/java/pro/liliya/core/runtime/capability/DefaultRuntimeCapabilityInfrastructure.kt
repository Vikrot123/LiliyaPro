package pro.liliya.core.runtime.capability

import pro.liliya.core.runtime.module.DefaultRuntimeModuleCapabilityLifecycle
import pro.liliya.core.runtime.module.RuntimeModuleCapabilityLifecycle

class DefaultRuntimeCapabilityInfrastructure(
    private val lifecycleManager: RuntimeCapabilityLifecycleManager
) : RuntimeCapabilityInfrastructure {

    private val binder =
        DefaultRuntimeModuleCapabilityBinder(
            lifecycleManager
        )

    private val registry =
        DefaultRuntimeCapabilityDiscoveryRegistry().apply {
            register(DefaultRuntimeCapabilityDiscovery())
        }

    private val moduleLifecycle =
        DefaultRuntimeModuleCapabilityLifecycle(
            binder,
            registry
        )

    override fun lifecycleManager(): RuntimeCapabilityLifecycleManager {
        return lifecycleManager
    }

    override fun lifecycle(): RuntimeModuleCapabilityBinder {
        return binder
    }

    override fun discoveryRegistry(): RuntimeCapabilityDiscoveryRegistry {
        return registry
    }

    override fun moduleCapabilityLifecycle(): RuntimeModuleCapabilityLifecycle {
        return moduleLifecycle
    }
}
