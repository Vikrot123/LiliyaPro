package pro.liliya.core.runtime.capability

import pro.liliya.core.module.LiliyaModule

class DefaultRuntimeCapabilityDiscoveryRegistry :
    RuntimeCapabilityDiscoveryRegistry {

    private val discoveries =
        mutableListOf<RuntimeCapabilityDiscovery>()

    override fun register(
        discovery: RuntimeCapabilityDiscovery
    ) {
        discoveries.add(discovery)
    }

    override fun discover(
        module: LiliyaModule
    ): RuntimeCapabilityProvider? {

        return discoveries.firstNotNullOfOrNull { discovery ->
            discovery.discover(module)
        }
    }
}
