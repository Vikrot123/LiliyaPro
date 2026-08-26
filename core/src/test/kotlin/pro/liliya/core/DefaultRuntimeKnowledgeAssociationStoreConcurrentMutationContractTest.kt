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
import pro.liliya.core.runtime.intelligence.knowledge.association.DefaultRuntimeKnowledgeAssociator
import pro.liliya.core.runtime.intelligence.knowledge.association.RuntimeKnowledgeAssociationType
import pro.liliya.core.runtime.intelligence.knowledge.association.store.DefaultRuntimeKnowledgeAssociationStore

class DefaultRuntimeKnowledgeAssociationStoreConcurrentMutationContractTest {

    @Test
    fun concurrent_append_and_remove_last_are_safe() {

        val store =
            DefaultRuntimeKnowledgeAssociationStore()

        val source =
            RuntimeKnowledge(
                statement = "association source",
                confidence = 0.9,
                source = RuntimeKnowledgeSource.EXPERIENCE,
                createdAt = 1L
            )

        val target =
            RuntimeKnowledge(
                statement = "association target",
                confidence = 0.9,
                source = RuntimeKnowledgeSource.EXPERIENCE,
                createdAt = 2L
            )

        val association =
            DefaultRuntimeKnowledgeAssociator()
                .associate(
                    source,
                    target,
                    RuntimeKnowledgeAssociationType.RELATED
                )

        repeat(512) {
            store.append(
                association
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
                            association
                        )

                        store.removeLast(
                            association
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
                            association
                        )

                        store.append(
                            association
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
                        store.associations()
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
            "first association writer must finish"
        )

        assertTrue(
            !secondWriter.isAlive,
            "second association writer must finish"
        )

        assertTrue(
            !reader.isAlive,
            "association reader must finish"
        )

        assertNull(
            failure.get(),
            "association store must tolerate concurrent append, removeLast and snapshot"
        )
    }
}
