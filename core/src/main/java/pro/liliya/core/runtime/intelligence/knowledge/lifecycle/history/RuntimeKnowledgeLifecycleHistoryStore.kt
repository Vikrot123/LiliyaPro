package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

interface RuntimeKnowledgeLifecycleHistoryStore {

    fun append(
        knowledge: RuntimeKnowledge,
        entry: RuntimeKnowledgeLifecycleHistoryEntry
    )

    fun history(
        knowledge: RuntimeKnowledge
    ): List<RuntimeKnowledgeLifecycleHistoryEntry>
}
