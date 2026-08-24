package pro.liliya.core.runtime.intelligence.memory.installer

import pro.liliya.core.runtime.intelligence.memory.registry.RuntimeMemoryRegistry

interface RuntimeMemoryProviderInstaller {

    fun install(
        registry: RuntimeMemoryRegistry
    )
}
