package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotNull

import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryType
import pro.liliya.core.runtime.intelligence.memory.installer.DefaultRuntimeMemoryProviderInstaller
import pro.liliya.core.runtime.intelligence.memory.registry.DefaultRuntimeMemoryRegistry

class RuntimeMemoryProviderInstallerDuplicateContractTest {

    @Test
    fun installer_should_be_safe_when_called_twice() {

        val registry =
            DefaultRuntimeMemoryRegistry()

        val installer =
            DefaultRuntimeMemoryProviderInstaller()

        installer.install(registry)
        installer.install(registry)

        assertNotNull(
            registry.provider(
                RuntimeMemoryType.SEMANTIC
            )
        )
    }
}
