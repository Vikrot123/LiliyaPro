package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.executor

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.decision.RuntimeKnowledgeLifecycleDecision
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.decision.RuntimeKnowledgeLifecycleDecisionQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.transition.RuntimeKnowledgeLifecycleTransitionManager

class DefaultRuntimeKnowledgeLifecycleExecutor(
    private val decisionQuery: RuntimeKnowledgeLifecycleDecisionQuery,
    private val transitionManager: RuntimeKnowledgeLifecycleTransitionManager
) : RuntimeKnowledgeLifecycleExecutor {

    override fun execute(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeLifecycleExecutionResult {

        val decision = decisionQuery.decide(knowledge)

        return when (decision) {

            RuntimeKnowledgeLifecycleDecision.KEEP ->
                RuntimeKnowledgeLifecycleExecutionResult(false)

            RuntimeKnowledgeLifecycleDecision.REVIEW ->
                RuntimeKnowledgeLifecycleExecutionResult(
                    transitionManager.transition(
                        knowledge,
                        RuntimeKnowledgeLifecycleState.REVIEW
                    )
                )

            RuntimeKnowledgeLifecycleDecision.ARCHIVE ->
                RuntimeKnowledgeLifecycleExecutionResult(
                    transitionManager.transition(
                        knowledge,
                        RuntimeKnowledgeLifecycleState.ARCHIVED
                    )
                )
        }
    }
}
