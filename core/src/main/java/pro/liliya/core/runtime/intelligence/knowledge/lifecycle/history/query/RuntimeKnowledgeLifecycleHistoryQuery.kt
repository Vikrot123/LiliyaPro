package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.query

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.RuntimeKnowledgeLifecycleHistoryEntry

interface RuntimeKnowledgeLifecycleHistoryQuery {

    fun lastTransition(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeLifecycleHistoryEntry?

    fun transitionCount(
        knowledge: RuntimeKnowledge
    ): Int
}
