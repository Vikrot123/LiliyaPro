package pro.liliya.core.runtime.capability

import pro.liliya.core.module.LiliyaModule

interface RuntimeCapabilityDiscovery {

    fun discover(
        module: LiliyaModule
    ): RuntimeCapabilityProvider?
}
