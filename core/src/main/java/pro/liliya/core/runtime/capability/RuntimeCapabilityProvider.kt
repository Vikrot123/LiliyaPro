package pro.liliya.core.runtime.capability

interface RuntimeCapabilityProvider {

    fun capabilities(): List<RuntimeCapabilityDefinition>

}
