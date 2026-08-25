package pro.liliya.core.runtime.intelligence.memory.installer

import pro.liliya.core.runtime.intelligence.memory.adapter.RuntimeKnowledgeMemoryAdapter
import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryType
import pro.liliya.core.runtime.intelligence.memory.capability.RuntimeMemoryCapability
import pro.liliya.core.runtime.intelligence.memory.registry.RuntimeMemoryRegistry
import pro.liliya.core.runtime.intelligence.knowledge.integration.RuntimeKnowledgeMemory

class DefaultRuntimeMemoryProviderInstaller(
    private val knowledgeMemory: RuntimeKnowledgeMemory
) : RuntimeMemoryProviderInstaller {

    override fun install(
        registry: RuntimeMemoryRegistry
    ) {
        registry.register(
            RuntimeMemoryType.SEMANTIC,
            RuntimeKnowledgeMemoryAdapter(
                knowledgeMemory
            ),
            setOf(
                RuntimeMemoryCapability.READ,
                RuntimeMemoryCapability.WRITE
            )
        )
    }
}
