package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.integration.DefaultRuntimeKnowledgeMemory
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.DefaultRuntimeKnowledgeLifecycleHistoryStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.integration.DefaultRuntimeKnowledgeLifecycleMemory
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state.DefaultRuntimeKnowledgeLifecycleStateStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.transition.DefaultRuntimeKnowledgeLifecycleTransitionManager
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.query.DefaultRuntimeKnowledgeLifecycleStateQuery

class RuntimeKnowledgeLifecycleSupersessionContractTest {

    @Test
    fun stronger_equivalent_candidate_supersedes_active_knowledge() {
        val fixture =
            fixture()

        val existing =
            knowledge(
                "runtime stable operational knowledge",
                0.70,
                1L
            )

        val stronger =
            knowledge(
                "Runtime stable operational knowledge.",
                0.95,
                2L
            )

        fixture.lifecycle.create(existing)
        fixture.lifecycle.create(stronger)

        assertEquals(
            RuntimeKnowledgeLifecycleState.ARCHIVED,
            fixture.memory.getLifecycleState(existing)
        )

        assertEquals(
            RuntimeKnowledgeLifecycleState.ACTIVE,
            fixture.memory.getLifecycleState(stronger)
        )

        assertEquals(
            listOf(stronger),
            fixture.memory.availableKnowledge()
        )

        val retrieved =
            fixture.memory.retrieveRelevant(
                "runtime stable operational knowledge"
            )

        assertTrue(
            retrieved.any {
                it.knowledge == stronger
            }
        )

        assertFalse(
            retrieved.any {
                it.knowledge == existing
            }
        )
    }

    @Test
    fun weaker_equivalent_candidate_cannot_supersede_active_knowledge() {
        val fixture =
            fixture()

        val existing =
            knowledge(
                "runtime stable operational knowledge",
                0.95,
                1L
            )

        val weaker =
            knowledge(
                "Runtime stable operational knowledge!",
                0.60,
                2L
            )

        fixture.lifecycle.create(existing)
        fixture.lifecycle.create(weaker)

        assertEquals(
            RuntimeKnowledgeLifecycleState.ACTIVE,
            fixture.memory.getLifecycleState(existing)
        )

        assertEquals(
            null,
            fixture.memory.getLifecycleState(weaker)
        )

        assertEquals(
            listOf(existing),
            fixture.memory.availableKnowledge()
        )
    }

    @Test
    fun supersession_preserves_archived_history_of_previous_winner() {
        val fixture =
            fixture()

        val existing =
            knowledge(
                "runtime historical knowledge",
                0.70,
                1L
            )

        val stronger =
            knowledge(
                "Runtime historical knowledge.",
                0.95,
                2L
            )

        fixture.lifecycle.create(existing)
        fixture.lifecycle.create(stronger)

        val history =
            fixture.historyStore.history(
                existing
            )

        assertEquals(
            listOf(
                RuntimeKnowledgeLifecycleState.ACTIVE,
                RuntimeKnowledgeLifecycleState.REVIEW,
                RuntimeKnowledgeLifecycleState.ARCHIVED
            ),
            history.map {
                it.to
            }
        )
    }

    private fun fixture(): Fixture {
        val stateStore =
            DefaultRuntimeKnowledgeLifecycleStateStore()

        val historyStore =
            DefaultRuntimeKnowledgeLifecycleHistoryStore()

        val transitionManager =
            DefaultRuntimeKnowledgeLifecycleTransitionManager(
                DefaultRuntimeKnowledgeLifecycleStateQuery(
                    stateStore
                ),
                stateStore,
                historyStore
            )

        val memory =
            DefaultRuntimeKnowledgeMemory(
                lifecycleStateStore = stateStore
            )

        return Fixture(
            memory = memory,
            historyStore = historyStore,
            lifecycle =
                DefaultRuntimeKnowledgeLifecycleMemory(
                    stateStore = stateStore,
                    historyStore = historyStore,
                    transitionManager = transitionManager,
                    knowledgeMemory = memory
                )
        )
    }

    private fun knowledge(
        statement: String,
        confidence: Double,
        createdAt: Long
    ) =
        RuntimeKnowledge(
            statement = statement,
            confidence = confidence,
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = createdAt
        )

    private data class Fixture(
        val memory: DefaultRuntimeKnowledgeMemory,
        val historyStore:
            DefaultRuntimeKnowledgeLifecycleHistoryStore,
        val lifecycle:
            DefaultRuntimeKnowledgeLifecycleMemory
    )
}
