package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.summary

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.query.RuntimeKnowledgeLifecycleHistoryQuery

class DefaultRuntimeKnowledgeLifecycleSummaryQuery(
    private val historyQuery: RuntimeKnowledgeLifecycleHistoryQuery
) : RuntimeKnowledgeLifecycleSummaryQuery {

    override fun summary(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeLifecycleSummary {

        val history =
            historyQuery.history(knowledge)

        return RuntimeKnowledgeLifecycleSummary(
            currentState = history.lastOrNull()?.to,
            transitionCount = history.size,
            firstState = history.firstOrNull()?.to,
            lastState = history.lastOrNull()?.to
        )
    }
}
