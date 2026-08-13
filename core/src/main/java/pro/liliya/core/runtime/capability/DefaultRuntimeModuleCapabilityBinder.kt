package pro.liliya.core.runtime.capability

import pro.liliya.core.runtime.control.RuntimeCommand

class DefaultRuntimeModuleCapabilityBinder(
    private val lifecycleManager: RuntimeCapabilityLifecycleManager =
        DefaultRuntimeCapabilityLifecycleManager()
) : RuntimeModuleCapabilityBinder {

    override fun bind(
        provider: RuntimeCapabilityProvider
    ) {
        provider.capabilities().forEach { definition ->

            lifecycleManager.register(
                RuntimeManagedCapability(
                    definition = definition
                )
            )

            lifecycleManager.activate(
                definition.command
            )
        }
    }

    override fun unbind(
        provider: RuntimeCapabilityProvider
    ) {
        provider.capabilities().forEach { definition ->

            lifecycleManager.disable(
                definition.command
            )

            lifecycleManager.remove(
                definition.command
            )
        }
    }
}
