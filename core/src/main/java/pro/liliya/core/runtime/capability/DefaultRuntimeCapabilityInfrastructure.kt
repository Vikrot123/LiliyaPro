package pro.liliya.core.runtime.capability

class DefaultRuntimeCapabilityInfrastructure(
    private val lifecycleManager: RuntimeCapabilityLifecycleManager =
        DefaultRuntimeCapabilityLifecycleManager()
) : RuntimeCapabilityInfrastructure {

    private val binder =
        DefaultRuntimeModuleCapabilityBinder(
            lifecycleManager
        )

    private val registry =
        DefaultRuntimeCapabilityDiscoveryRegistry().apply {
            register(DefaultRuntimeCapabilityDiscovery())
        }

    override fun lifecycleManager(): RuntimeCapabilityLifecycleManager {
        return lifecycleManager
    }

    override fun lifecycle(): RuntimeModuleCapabilityBinder {
        return binder
    }

    override fun discoveryRegistry(): RuntimeCapabilityDiscoveryRegistry {
        return registry
    }
}
