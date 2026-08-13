package pro.liliya.core.runtime.capability

interface RuntimeModuleCapabilityBinder {

    fun bind(
        provider: RuntimeCapabilityProvider
    )

    fun unbind(
        provider: RuntimeCapabilityProvider
    )
}
