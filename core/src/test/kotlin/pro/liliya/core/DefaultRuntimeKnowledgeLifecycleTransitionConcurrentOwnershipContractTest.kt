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

class DefaultRuntimeKnowledgeLifecycleTransitionConcurrentOwnershipContractTest {

    @Test
    fun concurrent_initial_transition_must_commit_once() {

        repeat(10_000) { iteration ->

            val stateStore =
                DefaultRuntimeKnowledgeLifecycleStateStore()

            val historyStore =
                DefaultRuntimeKnowledgeLifecycleHistoryStore()

            val manager =
                DefaultRuntimeKnowledgeLifecycleTransitionManager(
                    DefaultRuntimeKnowledgeLifecycleStateQuery(
                        stateStore
                    ),
                    stateStore,
                    historyStore
                )

            val knowledge =
                RuntimeKnowledge(
                    statement =
                        "concurrent transition $iteration",
                    confidence = 0.9,
                    source =
                        RuntimeKnowledgeSource.EXPERIENCE,
                    createdAt = iteration.toLong()
                )

            val start =
                CountDownLatch(1)

            val ready =
                CountDownLatch(2)

            val successes =
                AtomicInteger(0)

            val failure =
                AtomicReference<Throwable?>(null)

            fun launchTransition() =
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
                launchTransition()

            val second =
                launchTransition()

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
                "first transition thread must finish"
            )

            assertTrue(
                !second.isAlive,
                "second transition thread must finish"
            )

            assertNull(
                failure.get(),
                "concurrent transition must not fail"
            )

            assertEquals(
                1,
                successes.get(),
                "initial lifecycle transition must commit exactly once"
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
                "initial lifecycle transition must create exactly one history entry"
            )
        }
    }
}
