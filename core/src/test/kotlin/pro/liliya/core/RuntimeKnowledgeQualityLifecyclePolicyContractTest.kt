package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.decision.DefaultRuntimeKnowledgeLifecycleDecisionQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.decision.RuntimeKnowledgeLifecycleDecision
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.summary.RuntimeKnowledgeLifecycleSummary
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.summary.RuntimeKnowledgeLifecycleSummaryQuery

class RuntimeKnowledgeQualityLifecyclePolicyContractTest {

    @Test
    fun high_quality_active_knowledge_is_kept() {
        assertDecision(
            confidence = 0.80,
            state = RuntimeKnowledgeLifecycleState.ACTIVE,
            expected =
                RuntimeKnowledgeLifecycleDecision.KEEP
        )
    }

    @Test
    fun medium_priority_knowledge_enters_review() {
        assertDecision(
            confidence = 0.60,
            state = RuntimeKnowledgeLifecycleState.ACTIVE,
            expected =
                RuntimeKnowledgeLifecycleDecision.REVIEW
        )
    }

    @Test
    fun rejected_active_knowledge_enters_review_before_archive() {
        assertDecision(
            confidence = 0.30,
            state = RuntimeKnowledgeLifecycleState.ACTIVE,
            expected =
                RuntimeKnowledgeLifecycleDecision.REVIEW
        )
    }

    @Test
    fun rejected_knowledge_already_in_review_is_archived() {
        assertDecision(
            confidence = 0.30,
            state = RuntimeKnowledgeLifecycleState.REVIEW,
            expected =
                RuntimeKnowledgeLifecycleDecision.ARCHIVE
        )
    }

    @Test
    fun archived_state_remains_authoritative() {
        assertDecision(
            confidence = 0.95,
            state = RuntimeKnowledgeLifecycleState.ARCHIVED,
            expected =
                RuntimeKnowledgeLifecycleDecision.ARCHIVE
        )
    }

    @Test
    fun historical_review_threshold_is_preserved() {
        val query =
            query(
                state =
                    RuntimeKnowledgeLifecycleState.ACTIVE,
                transitionCount = 3
            )

        assertEquals(
            RuntimeKnowledgeLifecycleDecision.REVIEW,
            query.decide(
                knowledge(0.80)
            )
        )
    }

    private fun assertDecision(
        confidence: Double,
        state: RuntimeKnowledgeLifecycleState,
        expected: RuntimeKnowledgeLifecycleDecision
    ) {
        assertEquals(
            expected,
            query(state).decide(
                knowledge(confidence)
            )
        )
    }

    private fun query(
        state: RuntimeKnowledgeLifecycleState,
        transitionCount: Int = 1
    ) =
        DefaultRuntimeKnowledgeLifecycleDecisionQuery(
            object :
                RuntimeKnowledgeLifecycleSummaryQuery {

                override fun summary(
                    knowledge: RuntimeKnowledge
                ): RuntimeKnowledgeLifecycleSummary {

                    return RuntimeKnowledgeLifecycleSummary(
                        currentState = state,
                        transitionCount = transitionCount,
                        firstState =
                            RuntimeKnowledgeLifecycleState.ACTIVE,
                        lastState = state
                    )
                }
            }
        )

    private fun knowledge(
        confidence: Double
    ) =
        RuntimeKnowledge(
            statement = "quality lifecycle knowledge",
            confidence = confidence,
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = 1L
        )
}
