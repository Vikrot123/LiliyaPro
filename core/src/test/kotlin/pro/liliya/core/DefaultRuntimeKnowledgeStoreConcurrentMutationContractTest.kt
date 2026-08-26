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
import pro.liliya.core.runtime.intelligence.knowledge.store.DefaultRuntimeKnowledgeStore

class DefaultRuntimeKnowledgeStoreConcurrentMutationContractTest {

    @Test
    fun concurrent_append_remove_and_snapshot_are_safe() {

        val store =
            DefaultRuntimeKnowledgeStore()

        val knowledge =
            RuntimeKnowledge(
                statement = "concurrent knowledge",
                confidence = 0.9,
                source = RuntimeKnowledgeSource.EXPERIENCE,
                createdAt = 1L
            )

        repeat(512) {
            store.append(
                knowledge
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
                            knowledge
                        )

                        store.removeLast(
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
                            knowledge
                        )

                        store.append(
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

        val reader =
            thread {
                try {
                    start.countDown()

                    repeat(100_000) {
                        store.knowledge()
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
            "first writer must finish"
        )

        assertTrue(
            !secondWriter.isAlive,
            "second writer must finish"
        )

        assertTrue(
            !reader.isAlive,
            "reader must finish"
        )

        assertNull(
            failure.get(),
            "knowledge store must tolerate concurrent append, removeLast and snapshot"
        )
    }
}
