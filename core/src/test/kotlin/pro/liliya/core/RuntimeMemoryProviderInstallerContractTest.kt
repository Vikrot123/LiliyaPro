package pro.liliya.core

import kotlin.test.Test
import pro.liliya.core.runtime.intelligence.knowledge.integration.DefaultRuntimeKnowledgeMemory
import kotlin.test.assertNotNull
import kotlin.test.assertEquals

import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryType
import pro.liliya.core.runtime.intelligence.memory.capability.RuntimeMemoryCapability
import pro.liliya.core.runtime.intelligence.memory.installer.DefaultRuntimeMemoryProviderInstaller
import pro.liliya.core.runtime.intelligence.memory.registry.DefaultRuntimeMemoryRegistry

class RuntimeMemoryProviderInstallerContractTest {

    @Test
    fun installer_should_register_semantic_memory_provider() {

        val registry =
            DefaultRuntimeMemoryRegistry()

        val installer =
            DefaultRuntimeMemoryProviderInstaller(DefaultRuntimeKnowledgeMemory())

        installer.install(
            registry
        )

        assertNotNull(
            registry.provider(
                RuntimeMemoryType.SEMANTIC
            )
        )

        assertEquals(
            setOf(
                RuntimeMemoryCapability.READ,
                RuntimeMemoryCapability.WRITE
            ),
            registry.capabilities(
                RuntimeMemoryType.SEMANTIC
            )
        )
    }
}
