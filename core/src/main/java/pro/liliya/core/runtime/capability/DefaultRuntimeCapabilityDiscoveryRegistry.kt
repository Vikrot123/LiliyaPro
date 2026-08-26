package pro.liliya.core.runtime.capability

import pro.liliya.core.module.LiliyaModule

class DefaultRuntimeCapabilityDiscoveryRegistry :
    RuntimeCapabilityDiscoveryRegistry {

    private val discoveries =
        mutableListOf<RuntimeCapabilityDiscovery>()

    override fun register(
        discovery: RuntimeCapabilityDiscovery
    ) {
        synchronized(discoveries) {
            discoveries.add(discovery)
        }
    }

    override fun discover(
        module: LiliyaModule
    ): RuntimeCapabilityProvider? {

        val snapshot =
            synchronized(discoveries) {
                discoveries.toList()
            }

        return snapshot.firstNotNullOfOrNull { discovery ->
            discovery.discover(module)
        }
    }
}
