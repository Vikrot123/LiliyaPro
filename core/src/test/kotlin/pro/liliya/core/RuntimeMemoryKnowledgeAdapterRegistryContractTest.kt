package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertEquals

import pro.liliya.core.runtime.intelligence.memory.adapter.RuntimeKnowledgeMemoryAdapter
import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryType
import pro.liliya.core.runtime.intelligence.memory.capability.RuntimeMemoryCapability
import pro.liliya.core.runtime.intelligence.memory.registry.DefaultRuntimeMemoryRegistry
import pro.liliya.core.runtime.intelligence.knowledge.integration.DefaultRuntimeKnowledgeMemory

class RuntimeMemoryKnowledgeAdapterRegistryContractTest {

    @Test
    fun knowledge_memory_adapter_can_be_registered_as_semantic_memory() {

        val registry =
            DefaultRuntimeMemoryRegistry()

        val adapter =
            RuntimeKnowledgeMemoryAdapter(
                DefaultRuntimeKnowledgeMemory()
            )

        registry.register(
            RuntimeMemoryType.SEMANTIC,
            adapter,
            setOf(
                RuntimeMemoryCapability.READ,
                RuntimeMemoryCapability.WRITE
            )
        )

        assertNotNull(
            registry.provider(
                RuntimeMemoryType.SEMANTIC
            )
        )

        assertEquals(
            2,
            registry.capabilities(
                RuntimeMemoryType.SEMANTIC
            ).size
        )
    }
}
