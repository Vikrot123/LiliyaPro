package pro.liliya.core.runtime.module

import pro.liliya.core.module.LiliyaModule
import pro.liliya.core.runtime.capability.RuntimeCapabilityDiscovery
import pro.liliya.core.runtime.capability.DefaultRuntimeCapabilityDiscovery
import pro.liliya.core.runtime.capability.RuntimeModuleCapabilityBinder

class DefaultRuntimeModuleCapabilityLifecycle(
    private val binder: RuntimeModuleCapabilityBinder,
    private val discovery: RuntimeCapabilityDiscovery =
        DefaultRuntimeCapabilityDiscovery()
) : RuntimeModuleCapabilityLifecycle {

    override fun onModuleInit(
        module: LiliyaModule
    ) {
        val provider =
            discovery.discover(module)

        if (provider != null) {
            binder.bind(provider)
        }
    }

    override fun onModuleShutdown(
        module: LiliyaModule
    ) {
        val provider =
            discovery.discover(module)

        if (provider != null) {
            binder.unbind(provider)
        }
    }
}
