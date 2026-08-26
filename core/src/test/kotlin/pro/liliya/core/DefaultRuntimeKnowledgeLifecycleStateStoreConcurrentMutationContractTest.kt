package pro.liliya.core

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state.DefaultRuntimeKnowledgeLifecycleStateStore

class DefaultRuntimeKnowledgeLifecycleStateStoreConcurrentMutationContractTest {

    @Test
    fun concurrent_state_reads_and_writes_must_remain_safe() {
        val store =
            DefaultRuntimeKnowledgeLifecycleStateStore()

        val knowledge =
            List(64) { index ->
                RuntimeKnowledge(
                    statement = "concurrent state $index",
                    confidence = 0.9,
                    source =
                        RuntimeKnowledgeSource.EXPERIENCE,
                    createdAt = index.toLong()
                )
            }

        val ready =
            CountDownLatch(4)

        val start =
            CountDownLatch(1)

        val failure =
            AtomicReference<Throwable?>(null)

        fun launchWriter(offset: Int) =
            thread {
                try {
                    ready.countDown()

                    assertTrue(
                        start.await(
                            5,
                            TimeUnit.SECONDS
                        )
                    )

                    repeat(20_000) { iteration ->
                        val item =
                            knowledge[
                                (iteration + offset) %
                                    knowledge.size
                            ]

                        store.setState(
                            item,
                            if (iteration % 2 == 0) {
                                RuntimeKnowledgeLifecycleState.ACTIVE
                            } else {
                                RuntimeKnowledgeLifecycleState.REVIEW
                            }
                        )
                    }
                } catch (error: Throwable) {
                    failure.compareAndSet(
                        null,
                        error
                    )
                }
            }

        fun launchReader(offset: Int) =
            thread {
                try {
                    ready.countDown()

                    assertTrue(
                        start.await(
                            5,
                            TimeUnit.SECONDS
                        )
                    )

                    repeat(20_000) { iteration ->
                        store.getState(
                            knowledge[
                                (iteration + offset) %
                                    knowledge.size
                            ]
                        )
                    }
                } catch (error: Throwable) {
                    failure.compareAndSet(
                        null,
                        error
                    )
                }
            }

        val firstWriter =
            launchWriter(0)

        val secondWriter =
            launchWriter(17)

        val firstReader =
            launchReader(7)

        val secondReader =
            launchReader(31)

        assertTrue(
            ready.await(
                5,
                TimeUnit.SECONDS
            )
        )

        start.countDown()

        listOf(
            firstWriter,
            secondWriter,
            firstReader,
            secondReader
        ).forEach { worker ->
            worker.join(10_000)

            assertTrue(
                !worker.isAlive,
                "concurrent state-store worker must finish"
            )
        }

        assertNull(
            failure.get(),
            "concurrent state-store access must not fail"
        )

        knowledge.forEach { item ->
            store.setState(
                item,
                RuntimeKnowledgeLifecycleState.ARCHIVED
            )
        }

        knowledge.forEach { item ->
            assertEquals(
                RuntimeKnowledgeLifecycleState.ARCHIVED,
                store.getState(item)
            )
        }
    }
}
