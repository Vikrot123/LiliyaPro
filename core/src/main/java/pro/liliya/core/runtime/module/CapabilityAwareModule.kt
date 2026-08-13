package pro.liliya.core.runtime.module

import pro.liliya.core.runtime.capability.RuntimeCapabilityProvider

interface CapabilityAwareModule {

    fun capabilityProvider(): RuntimeCapabilityProvider

}
