package pro.liliya.core.runtime.capability

class DefaultRuntimeCapabilityInfrastructureProvider :
    RuntimeCapabilityInfrastructureProvider {

    override fun provide(): RuntimeCapabilityInfrastructure {

        val lifecycleManager =
            DefaultRuntimeCapabilityLifecycleManager()

        return DefaultRuntimeCapabilityInfrastructure(
            lifecycleManager
        )
    }
}
