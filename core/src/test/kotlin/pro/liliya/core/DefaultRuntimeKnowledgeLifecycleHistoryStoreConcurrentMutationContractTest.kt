package pro.liliya.core

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.DefaultRuntimeKnowledgeLifecycleHistoryStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.RuntimeKnowledgeLifecycleHistoryEntry

class DefaultRuntimeKnowledgeLifecycleHistoryStoreConcurrentMutationContractTest {

    @Test
    fun concurrent_append_remove_and_history_snapshot_are_safe() {

        val store =
            DefaultRuntimeKnowledgeLifecycleHistoryStore()

        val knowledge =
            RuntimeKnowledge(
                statement = "concurrent lifecycle history",
                confidence = 0.9,
                source = RuntimeKnowledgeSource.EXPERIENCE,
                createdAt = 1L
            )

        val entry =
            RuntimeKnowledgeLifecycleHistoryEntry(
                from = null,
                to = RuntimeKnowledgeLifecycleState.ACTIVE,
                timestamp = 1L
            )

        repeat(512) {
            store.append(
                knowledge,
                entry
            )
        }

        val start =
            CountDownLatch(1)

        val failure =
            AtomicReference<Throwable?>(null)

        val firstWriter =
            thread {
                try {
                    assertTrue(
                        start.await(
                            5,
                            TimeUnit.SECONDS
                        )
                    )

                    repeat(100_000) {
                        store.append(
                            knowledge,
                            entry
                        )

                        store.removeLast(
                            knowledge,
                            entry
                        )
                    }
                } catch (error: Throwable) {
                    failure.compareAndSet(
                        null,
                        error
                    )
                }
            }

        val secondWriter =
            thread {
                try {
                    assertTrue(
                        start.await(
                            5,
                            TimeUnit.SECONDS
                        )
                    )

                    repeat(100_000) {
                        store.removeLast(
                            knowledge,
                            entry
                        )

                        store.append(
                            knowledge,
                            entry
                        )
                    }
                } catch (error: Throwable) {
                    failure.compareAndSet(
                        null,
                        error
                    )
                }
            }

        val reader =
            thread {
                try {
                    start.countDown()

                    repeat(100_000) {
                        store.history(
                            knowledge
                        )
                    }
                } catch (error: Throwable) {
                    failure.compareAndSet(
                        null,
                        error
                    )
                }
            }

        firstWriter.join(15_000)
        secondWriter.join(15_000)
        reader.join(15_000)

        assertTrue(
            !firstWriter.isAlive,
            "first history writer must finish"
        )

        assertTrue(
            !secondWriter.isAlive,
            "second history writer must finish"
        )

        assertTrue(
            !reader.isAlive,
            "history reader must finish"
        )

        assertNull(
            failure.get(),
            "knowledge lifecycle history store must tolerate concurrent append, removeLast and history snapshot"
        )
    }
}
