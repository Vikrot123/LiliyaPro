package pro.liliya.core.runtime.capability

import pro.liliya.core.module.LiliyaModule

interface RuntimeCapabilityDiscoveryRegistry {

    fun register(
        discovery: RuntimeCapabilityDiscovery
    )

    fun discover(
        module: LiliyaModule
    ): RuntimeCapabilityProvider?
}
