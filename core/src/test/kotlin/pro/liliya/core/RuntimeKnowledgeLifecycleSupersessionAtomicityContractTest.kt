package pro.liliya.core

import java.util.concurrent.CountDownLatch
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.integration.DefaultRuntimeKnowledgeMemory
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.DefaultRuntimeKnowledgeLifecycleHistoryStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.integration.DefaultRuntimeKnowledgeLifecycleMemory
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.query.DefaultRuntimeKnowledgeLifecycleStateQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state.DefaultRuntimeKnowledgeLifecycleStateStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.transition.DefaultRuntimeKnowledgeLifecycleTransitionManager
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.transition.RuntimeKnowledgeLifecycleTransitionManager

class RuntimeKnowledgeLifecycleSupersessionAtomicityContractTest {

    @Test
    fun failed_candidate_activation_restores_previous_winner_atomically() {
        val stateStore =
            DefaultRuntimeKnowledgeLifecycleStateStore()

        val historyStore =
            DefaultRuntimeKnowledgeLifecycleHistoryStore()

        val normalTransitionManager =
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

        val existing =
            knowledge(
                statement =
                    "runtime supersession rollback knowledge",
                confidence = 0.70,
                createdAt = 1L
            )

        val candidate =
            knowledge(
                statement =
                    "Runtime supersession rollback knowledge.",
                confidence = 0.95,
                createdAt = 2L
            )

        val normalLifecycle =
            DefaultRuntimeKnowledgeLifecycleMemory(
                stateStore = stateStore,
                historyStore = historyStore,
                transitionManager = normalTransitionManager,
                knowledgeMemory = memory
            )

        normalLifecycle.create(
            existing
        )

        val failingTransitionManager =
            object :
                RuntimeKnowledgeLifecycleTransitionManager {

                override fun transition(
                    knowledge: RuntimeKnowledge,
                    target: RuntimeKnowledgeLifecycleState
                ): Boolean {
                    if (
                        knowledge == candidate &&
                        target ==
                            RuntimeKnowledgeLifecycleState.ACTIVE
                    ) {
                        throw IllegalStateException(
                            "candidate activation failed"
                        )
                    }

                    return normalTransitionManager.transition(
                        knowledge,
                        target
                    )
                }
            }

        val failingLifecycle =
            DefaultRuntimeKnowledgeLifecycleMemory(
                stateStore = stateStore,
                historyStore = historyStore,
                transitionManager = failingTransitionManager,
                knowledgeMemory = memory
            )

        assertFailsWith<IllegalStateException> {
            failingLifecycle.create(
                candidate
            )
        }

        assertEquals(
            listOf(existing),
            memory.availableKnowledge()
        )

        assertEquals(
            RuntimeKnowledgeLifecycleState.ACTIVE,
            memory.getLifecycleState(existing)
        )

        assertEquals(
            null,
            memory.getLifecycleState(candidate)
        )

        assertEquals(
            listOf(
                RuntimeKnowledgeLifecycleState.ACTIVE
            ),
            historyStore
                .history(existing)
                .map {
                    it.to
                }
        )
    }

    @Test
    fun concurrent_stronger_candidates_converge_on_highest_confidence_winner() {
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

        val lifecycle =
            DefaultRuntimeKnowledgeLifecycleMemory(
                stateStore = stateStore,
                historyStore = historyStore,
                transitionManager = transitionManager,
                knowledgeMemory = memory
            )

        val initial =
            knowledge(
                "runtime concurrent winner knowledge",
                0.60,
                1L
            )

        val medium =
            knowledge(
                "Runtime concurrent winner knowledge.",
                0.80,
                2L
            )

        val strongest =
            knowledge(
                "RUNTIME concurrent winner knowledge!",
                0.95,
                3L
            )

        lifecycle.create(
            initial
        )

        val ready =
            CountDownLatch(2)

        val start =
            CountDownLatch(1)

        val errors =
            CopyOnWriteArrayList<Throwable>()

        val threads =
            listOf(
                medium,
                strongest
            ).map { candidate ->
                thread(start = true) {
                    try {
                        ready.countDown()
                        start.await()

                        lifecycle.create(
                            candidate
                        )
                    } catch (error: Throwable) {
                        errors += error
                    }
                }
            }

        ready.await()
        start.countDown()

        threads.forEach {
            it.join()
        }

        assertTrue(
            errors.isEmpty(),
            "concurrent supersession must not fail: $errors"
        )

        assertEquals(
            listOf(strongest),
            memory.availableKnowledge()
        )

        assertEquals(
            RuntimeKnowledgeLifecycleState.ACTIVE,
            memory.getLifecycleState(strongest)
        )

        assertTrue(
            memory
                .retrieveRelevant(
                    "runtime concurrent winner knowledge"
                )
                .all {
                    it.knowledge == strongest
                }
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
            source =
                RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = createdAt
        )
}
