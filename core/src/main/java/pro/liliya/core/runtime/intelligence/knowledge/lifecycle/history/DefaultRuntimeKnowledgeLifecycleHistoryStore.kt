package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

class DefaultRuntimeKnowledgeLifecycleHistoryStore :
    RuntimeKnowledgeLifecycleHistoryStore {

    private val history =
        mutableMapOf<
            RuntimeKnowledge,
            MutableList<RuntimeKnowledgeLifecycleHistoryEntry>
        >()

    override fun append(
        knowledge: RuntimeKnowledge,
        entry: RuntimeKnowledgeLifecycleHistoryEntry
    ) {
        val entries =
            history.getOrPut(knowledge) {
                mutableListOf()
            }

        entries += entry
    }

    override fun history(
        knowledge: RuntimeKnowledge
    ): List<RuntimeKnowledgeLifecycleHistoryEntry> {
        return history[knowledge]
            ?.toList()
            ?: emptyList()
    }

    override fun removeLast(
        knowledge: RuntimeKnowledge,
        entry: RuntimeKnowledgeLifecycleHistoryEntry
    ) {
        val entries =
            history[knowledge]
                ?: return

        if (entries.lastOrNull() != entry) {
            return
        }

        entries.removeAt(
            entries.lastIndex
        )

        if (entries.isEmpty()) {
            history.remove(knowledge)
        }
    }
}
