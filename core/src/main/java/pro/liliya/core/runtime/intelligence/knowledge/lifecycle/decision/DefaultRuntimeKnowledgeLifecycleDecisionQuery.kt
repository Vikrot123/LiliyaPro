package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.decision

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.summary.RuntimeKnowledgeLifecycleSummaryQuery

class DefaultRuntimeKnowledgeLifecycleDecisionQuery(
    private val summaryQuery: RuntimeKnowledgeLifecycleSummaryQuery
) : RuntimeKnowledgeLifecycleDecisionQuery {

    override fun decide(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeLifecycleDecision {

        val summary = summaryQuery.summary(knowledge)

        return when {
            summary.lastState ==
                RuntimeKnowledgeLifecycleState.ARCHIVED ->
                RuntimeKnowledgeLifecycleDecision.ARCHIVE

            summary.transitionCount > 2 ->
                RuntimeKnowledgeLifecycleDecision.REVIEW

            else ->
                RuntimeKnowledgeLifecycleDecision.KEEP
        }
    }
}
