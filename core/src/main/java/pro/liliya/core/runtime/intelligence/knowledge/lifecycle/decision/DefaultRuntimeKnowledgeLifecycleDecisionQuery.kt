package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.decision

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.summary.RuntimeKnowledgeLifecycleSummaryQuery
import pro.liliya.core.runtime.intelligence.knowledge.priority.RuntimeKnowledgePriorityLevel
import pro.liliya.core.runtime.intelligence.knowledge.quality.DefaultRuntimeKnowledgeQualityEvaluator
import pro.liliya.core.runtime.intelligence.knowledge.quality.RuntimeKnowledgeQualityEvaluator

class DefaultRuntimeKnowledgeLifecycleDecisionQuery(
    private val summaryQuery: RuntimeKnowledgeLifecycleSummaryQuery,
    private val qualityEvaluator: RuntimeKnowledgeQualityEvaluator =
        DefaultRuntimeKnowledgeQualityEvaluator()
) : RuntimeKnowledgeLifecycleDecisionQuery {

    override fun decide(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeLifecycleDecision {

        val summary =
            summaryQuery.summary(knowledge)

        val quality =
            qualityEvaluator.evaluate(knowledge)

        return when {
            summary.lastState ==
                RuntimeKnowledgeLifecycleState.ARCHIVED ->
                RuntimeKnowledgeLifecycleDecision.ARCHIVE

            !quality.validation.accepted &&
                summary.currentState ==
                    RuntimeKnowledgeLifecycleState.REVIEW ->
                RuntimeKnowledgeLifecycleDecision.ARCHIVE

            !quality.validation.accepted ->
                RuntimeKnowledgeLifecycleDecision.REVIEW

            quality.priority.level ==
                RuntimeKnowledgePriorityLevel.MEDIUM ->
                RuntimeKnowledgeLifecycleDecision.REVIEW

            quality.priority.level ==
                RuntimeKnowledgePriorityLevel.LOW ->
                RuntimeKnowledgeLifecycleDecision.REVIEW

            summary.transitionCount > 2 ->
                RuntimeKnowledgeLifecycleDecision.REVIEW

            else ->
                RuntimeKnowledgeLifecycleDecision.KEEP
        }
    }
}
