package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.decision.DefaultRuntimeKnowledgeLifecycleDecisionQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.decision.RuntimeKnowledgeLifecycleDecision
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
}
