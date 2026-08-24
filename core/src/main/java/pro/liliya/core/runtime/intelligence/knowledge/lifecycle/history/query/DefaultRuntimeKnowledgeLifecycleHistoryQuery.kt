package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.query

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.RuntimeKnowledgeLifecycleHistoryEntry
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.RuntimeKnowledgeLifecycleHistoryStore

class DefaultRuntimeKnowledgeLifecycleHistoryQuery(
    private val historyStore: RuntimeKnowledgeLifecycleHistoryStore
) : RuntimeKnowledgeLifecycleHistoryQuery {

    override fun lastTransition(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeLifecycleHistoryEntry? {

        return historyStore
            .history(knowledge)
            .lastOrNull()
    }

    override fun transitionCount(
        knowledge: RuntimeKnowledge
    ): Int {

        return historyStore
            .history(knowledge)
            .size
    }
}
