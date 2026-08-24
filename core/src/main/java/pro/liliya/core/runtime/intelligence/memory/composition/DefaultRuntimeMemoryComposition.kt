package pro.liliya.core.runtime.intelligence.memory.composition

import pro.liliya.core.runtime.intelligence.memory.access.DefaultRuntimeMemoryAccess
import pro.liliya.core.runtime.intelligence.memory.access.RuntimeMemoryAccess
import pro.liliya.core.runtime.intelligence.memory.installer.DefaultRuntimeMemoryProviderInstaller
import pro.liliya.core.runtime.intelligence.memory.installer.RuntimeMemoryProviderInstaller
import pro.liliya.core.runtime.intelligence.memory.registry.DefaultRuntimeMemoryRegistry
import pro.liliya.core.runtime.intelligence.memory.registry.RuntimeMemoryRegistry

class DefaultRuntimeMemoryComposition :
    RuntimeMemoryComposition {

    private val registry: RuntimeMemoryRegistry =
        DefaultRuntimeMemoryRegistry()

    private val access: RuntimeMemoryAccess =
        DefaultRuntimeMemoryAccess(
            registry
        )

    private val providerInstaller: RuntimeMemoryProviderInstaller =
        DefaultRuntimeMemoryProviderInstaller()

    init {
        providerInstaller.install(
            registry
        )
    }

    override fun registry(): RuntimeMemoryRegistry {
        return registry
    }

    override fun access(): RuntimeMemoryAccess {
        return access
    }
}
