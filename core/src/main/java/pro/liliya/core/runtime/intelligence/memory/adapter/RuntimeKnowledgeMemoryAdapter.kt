package pro.liliya.core.runtime.intelligence.memory.adapter

import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryEntry
import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryProvider
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.integration.RuntimeKnowledgeMemory

class RuntimeKnowledgeMemoryAdapter(
    private val knowledgeMemory: RuntimeKnowledgeMemory
) : RuntimeMemoryProvider {

    override fun store(
        entry: RuntimeMemoryEntry
    ) {
        knowledgeMemory.remember(
            RuntimeKnowledge(
                statement = entry.content,
                confidence = entry.confidence,
                source = RuntimeKnowledgeSource.EXPERIENCE,
                createdAt = entry.createdAt
            )
        )
    }

    override fun search(
        query: String
    ): List<RuntimeMemoryEntry> {

        return knowledgeMemory
            .query(query)
            .map {
                RuntimeMemoryEntry(
                    id = it.result.node.knowledge.statement,
                    content = it.result.node.knowledge.statement,
                    type = pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryType.SEMANTIC,
                    confidence = it.result.node.knowledge.confidence,
                    createdAt = it.result.node.knowledge.createdAt
                )
            }
    }

    override fun clear() {
        // Пока KnowledgeMemory не предоставляет clear().
        // Очистка будет добавлена отдельным контрактом.
    }
}
