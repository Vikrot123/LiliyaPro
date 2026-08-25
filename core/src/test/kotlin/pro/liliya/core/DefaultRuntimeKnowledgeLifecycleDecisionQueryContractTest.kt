package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.decision.DefaultRuntimeKnowledgeLifecycleDecisionQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.decision.RuntimeKnowledgeLifecycleDecision
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.summary.RuntimeKnowledgeLifecycleSummary
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.summary.RuntimeKnowledgeLifecycleSummaryQuery

class DefaultRuntimeKnowledgeLifecycleDecisionQueryContractTest {

    private fun knowledge(): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = "decision knowledge",
            confidence = 0.8,
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = 1L
        )
    }

    @Test
    fun archived_summary_returns_archive_decision() {

        val query =
            DefaultRuntimeKnowledgeLifecycleDecisionQuery(
                object : RuntimeKnowledgeLifecycleSummaryQuery {

                    override fun summary(
                        knowledge: RuntimeKnowledge
                    ): RuntimeKnowledgeLifecycleSummary {
                        return RuntimeKnowledgeLifecycleSummary(
                            currentState = null,
                            transitionCount = 1,
                            firstState = null,
                            lastState =
                                pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState.ARCHIVED
                        )
                    }
                }
            )

        assertEquals(
            RuntimeKnowledgeLifecycleDecision.ARCHIVE,
            query.decide(knowledge())
        )
    }

    @Test
    fun transition_count_two_returns_keep() {
        val query =
            DefaultRuntimeKnowledgeLifecycleDecisionQuery(
                object : RuntimeKnowledgeLifecycleSummaryQuery {
                    override fun summary(
                        knowledge: RuntimeKnowledge
                    ): RuntimeKnowledgeLifecycleSummary {
                        return RuntimeKnowledgeLifecycleSummary(
                            currentState = RuntimeKnowledgeLifecycleState.REVIEW,
                            transitionCount = 2,
                            firstState = RuntimeKnowledgeLifecycleState.ACTIVE,
                            lastState = RuntimeKnowledgeLifecycleState.REVIEW
                        )
                    }
                }
            )

        assertEquals(
            RuntimeKnowledgeLifecycleDecision.KEEP,
            query.decide(knowledge())
        )
    }

    @Test
    fun transition_count_three_returns_review() {
        val query =
            DefaultRuntimeKnowledgeLifecycleDecisionQuery(
                object : RuntimeKnowledgeLifecycleSummaryQuery {
                    override fun summary(
                        knowledge: RuntimeKnowledge
                    ): RuntimeKnowledgeLifecycleSummary {
                        return RuntimeKnowledgeLifecycleSummary(
                            currentState = RuntimeKnowledgeLifecycleState.REVIEW,
                            transitionCount = 3,
                            firstState = RuntimeKnowledgeLifecycleState.ACTIVE,
                            lastState = RuntimeKnowledgeLifecycleState.REVIEW
                        )
                    }
                }
            )

        assertEquals(
            RuntimeKnowledgeLifecycleDecision.REVIEW,
            query.decide(knowledge())
        )
    }

    @Test
    fun transition_count_zero_returns_keep() {
        val query =
            DefaultRuntimeKnowledgeLifecycleDecisionQuery(
                object : RuntimeKnowledgeLifecycleSummaryQuery {
                    override fun summary(
                        knowledge: RuntimeKnowledge
                    ): RuntimeKnowledgeLifecycleSummary {
                        return RuntimeKnowledgeLifecycleSummary(
                            currentState = null,
                            transitionCount = 0,
                            firstState = null,
                            lastState = null
                        )
                    }
                }
            )

        assertEquals(
            RuntimeKnowledgeLifecycleDecision.KEEP,
            query.decide(knowledge())
        )
    }

    @Test
    fun archived_state_has_priority_over_review_threshold() {
        val query =
            DefaultRuntimeKnowledgeLifecycleDecisionQuery(
                object : RuntimeKnowledgeLifecycleSummaryQuery {
                    override fun summary(
                        knowledge: RuntimeKnowledge
                    ): RuntimeKnowledgeLifecycleSummary {
                        return RuntimeKnowledgeLifecycleSummary(
                            currentState = RuntimeKnowledgeLifecycleState.ARCHIVED,
                            transitionCount = 3,
                            firstState = RuntimeKnowledgeLifecycleState.ACTIVE,
                            lastState = RuntimeKnowledgeLifecycleState.ARCHIVED
                        )
                    }
                }
            )

        assertEquals(
            RuntimeKnowledgeLifecycleDecision.ARCHIVE,
            query.decide(knowledge())
        )
    }

}
