package pro.liliya.core

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.DefaultRuntimeKnowledgeLifecycleHistoryStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.query.DefaultRuntimeKnowledgeLifecycleStateQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state.DefaultRuntimeKnowledgeLifecycleStateStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.transition.DefaultRuntimeKnowledgeLifecycleTransitionManager

class DefaultRuntimeKnowledgeLifecycleTransitionMultiManagerOwnershipContractTest {

    @Test
    fun concurrent_initial_transition_across_managers_must_commit_once() {

        repeat(10_000) { iteration ->

            val stateStore =
                DefaultRuntimeKnowledgeLifecycleStateStore()

            val historyStore =
                DefaultRuntimeKnowledgeLifecycleHistoryStore()

            val stateQuery =
                DefaultRuntimeKnowledgeLifecycleStateQuery(
                    stateStore
                )

            val firstManager =
                DefaultRuntimeKnowledgeLifecycleTransitionManager(
                    stateQuery,
                    stateStore,
                    historyStore
                )

            val secondManager =
                DefaultRuntimeKnowledgeLifecycleTransitionManager(
                    stateQuery,
                    stateStore,
                    historyStore
                )

            val knowledge =
                RuntimeKnowledge(
                    statement =
                        "multi-manager transition $iteration",
                    confidence = 0.9,
                    source =
                        RuntimeKnowledgeSource.EXPERIENCE,
                    createdAt = iteration.toLong()
                )

            val ready =
                CountDownLatch(2)

            val start =
                CountDownLatch(1)

            val successes =
                AtomicInteger(0)

            val failure =
                AtomicReference<Throwable?>(null)

            fun launch(
                manager: DefaultRuntimeKnowledgeLifecycleTransitionManager
            ) =
                thread {
                    try {
                        ready.countDown()

                        assertTrue(
                            start.await(
                                5,
                                TimeUnit.SECONDS
                            )
                        )

                        if (
                            manager.transition(
                                knowledge,
                                RuntimeKnowledgeLifecycleState.ACTIVE
                            )
                        ) {
                            successes.incrementAndGet()
                        }
                    } catch (error: Throwable) {
                        failure.compareAndSet(
                            null,
                            error
                        )
                    }
                }

            val first =
                launch(firstManager)

            val second =
                launch(secondManager)

            assertTrue(
                ready.await(
                    5,
                    TimeUnit.SECONDS
                )
            )

            start.countDown()

            first.join(5_000)
            second.join(5_000)

            assertTrue(
                !first.isAlive,
                "first manager transition must finish"
            )

            assertTrue(
                !second.isAlive,
                "second manager transition must finish"
            )

            assertNull(
                failure.get(),
                "cross-manager transition must not fail"
            )

            assertEquals(
                1,
                successes.get(),
                "shared lifecycle state must allow exactly one initial transition"
            )

            assertEquals(
                RuntimeKnowledgeLifecycleState.ACTIVE,
                stateStore.getState(
                    knowledge
                )
            )

            assertEquals(
                1,
                historyStore.history(
                    knowledge
                ).size,
                "shared lifecycle history must contain exactly one initial transition"
            )
        }
    }
}
