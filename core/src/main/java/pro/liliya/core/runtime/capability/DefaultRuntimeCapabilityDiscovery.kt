package pro.liliya.core.runtime.capability

import pro.liliya.core.module.LiliyaModule
import pro.liliya.core.runtime.module.CapabilityAwareModule

class DefaultRuntimeCapabilityDiscovery :
    RuntimeCapabilityDiscovery {

    override fun discover(
        module: LiliyaModule
    ): RuntimeCapabilityProvider? {

        return if (module is CapabilityAwareModule) {
            module.capabilityProvider()
        } else {
            null
        }
    }
}
