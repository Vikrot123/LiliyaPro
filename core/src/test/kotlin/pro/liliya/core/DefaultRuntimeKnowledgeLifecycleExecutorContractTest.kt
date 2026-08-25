package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.DefaultRuntimeKnowledgeLifecycleHistoryStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.decision.RuntimeKnowledgeLifecycleDecision
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.decision.RuntimeKnowledgeLifecycleDecisionQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.executor.DefaultRuntimeKnowledgeLifecycleExecutor
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.summary.RuntimeKnowledgeLifecycleSummary
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.summary.RuntimeKnowledgeLifecycleSummaryQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.transition.DefaultRuntimeKnowledgeLifecycleTransitionManager
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.query.DefaultRuntimeKnowledgeLifecycleStateQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state.DefaultRuntimeKnowledgeLifecycleStateStore

class DefaultRuntimeKnowledgeLifecycleExecutorContractTest {

    private fun knowledge(): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = "executor knowledge",
            confidence = 0.8,
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = 1L
        )
    }

    @Test
    fun review_decision_executes_transition() {

        val stateStore =
            DefaultRuntimeKnowledgeLifecycleStateStore()
            val historyStore = DefaultRuntimeKnowledgeLifecycleHistoryStore()

        val transitionManager =
            DefaultRuntimeKnowledgeLifecycleTransitionManager(
                DefaultRuntimeKnowledgeLifecycleStateQuery(stateStore),
                stateStore,
                historyStore
            )

        val executor =
            DefaultRuntimeKnowledgeLifecycleExecutor(
                object : RuntimeKnowledgeLifecycleDecisionQuery {

                    override fun decide(
                        knowledge: RuntimeKnowledge
                    ): RuntimeKnowledgeLifecycleDecision {
                        return RuntimeKnowledgeLifecycleDecision.REVIEW
                    }
                },
                transitionManager
            )

        val knowledge = knowledge()

        transitionManager.transition(
            knowledge,
            RuntimeKnowledgeLifecycleState.ACTIVE
        )

        assertTrue(
            executor.execute(knowledge).executed
        )
    }

    @Test
    fun archive_decision_executes_transition() {

        val stateStore =
            DefaultRuntimeKnowledgeLifecycleStateStore()
            val historyStore = DefaultRuntimeKnowledgeLifecycleHistoryStore()

        val transitionManager =
            DefaultRuntimeKnowledgeLifecycleTransitionManager(
                DefaultRuntimeKnowledgeLifecycleStateQuery(stateStore),
                stateStore,
                historyStore
            )

        val executor =
            DefaultRuntimeKnowledgeLifecycleExecutor(
                object : RuntimeKnowledgeLifecycleDecisionQuery {

                    override fun decide(
                        knowledge: RuntimeKnowledge
                    ): RuntimeKnowledgeLifecycleDecision {
                        return RuntimeKnowledgeLifecycleDecision.ARCHIVE
                    }
                },
                transitionManager
            )

        val knowledge = knowledge()

        transitionManager.transition(
            knowledge,
            RuntimeKnowledgeLifecycleState.ACTIVE
        )

        transitionManager.transition(
            knowledge,
            RuntimeKnowledgeLifecycleState.REVIEW
        )

        assertTrue(
            executor.execute(knowledge).executed
        )
    }


    @Test
    fun keep_decision_does_not_execute_transition() {

        val stateStore =
            DefaultRuntimeKnowledgeLifecycleStateStore()
            val historyStore = DefaultRuntimeKnowledgeLifecycleHistoryStore()

        val transitionManager =
            DefaultRuntimeKnowledgeLifecycleTransitionManager(
                DefaultRuntimeKnowledgeLifecycleStateQuery(stateStore),
                stateStore,
                historyStore
            )

        val executor =
            DefaultRuntimeKnowledgeLifecycleExecutor(
                object : RuntimeKnowledgeLifecycleDecisionQuery {

                    override fun decide(
                        knowledge: RuntimeKnowledge
                    ): RuntimeKnowledgeLifecycleDecision {
                        return RuntimeKnowledgeLifecycleDecision.KEEP
                    }
                },
                transitionManager
            )

        val result =
            executor.execute(knowledge())

        assertTrue(
            !result.executed
        )
    }

    @Test
    fun review_decision_reports_false_when_transition_is_rejected() {
        val stateStore = DefaultRuntimeKnowledgeLifecycleStateStore()
        val historyStore = DefaultRuntimeKnowledgeLifecycleHistoryStore()

        val transitionManager =
            DefaultRuntimeKnowledgeLifecycleTransitionManager(
                DefaultRuntimeKnowledgeLifecycleStateQuery(stateStore),
                stateStore,
                historyStore
            )

        val executor =
            DefaultRuntimeKnowledgeLifecycleExecutor(
                object : RuntimeKnowledgeLifecycleDecisionQuery {
                    override fun decide(
                        knowledge: RuntimeKnowledge
                    ): RuntimeKnowledgeLifecycleDecision {
                        return RuntimeKnowledgeLifecycleDecision.REVIEW
                    }
                },
                transitionManager
            )

        val knowledge = knowledge()

        transitionManager.transition(
            knowledge,
            RuntimeKnowledgeLifecycleState.ACTIVE
        )

        transitionManager.transition(
            knowledge,
            RuntimeKnowledgeLifecycleState.REVIEW
        )

        assertTrue(
            !executor.execute(knowledge).executed
        )
    }

    @Test
    fun archive_decision_reports_false_when_transition_is_rejected() {
        val stateStore = DefaultRuntimeKnowledgeLifecycleStateStore()
        val historyStore = DefaultRuntimeKnowledgeLifecycleHistoryStore()

        val transitionManager =
            DefaultRuntimeKnowledgeLifecycleTransitionManager(
                DefaultRuntimeKnowledgeLifecycleStateQuery(stateStore),
                stateStore,
                historyStore
            )

        val executor =
            DefaultRuntimeKnowledgeLifecycleExecutor(
                object : RuntimeKnowledgeLifecycleDecisionQuery {
                    override fun decide(
                        knowledge: RuntimeKnowledge
                    ): RuntimeKnowledgeLifecycleDecision {
                        return RuntimeKnowledgeLifecycleDecision.ARCHIVE
                    }
                },
                transitionManager
            )

        val knowledge = knowledge()

        transitionManager.transition(
            knowledge,
            RuntimeKnowledgeLifecycleState.ACTIVE
        )

        assertTrue(
            !executor.execute(knowledge).executed
        )
    }

}
