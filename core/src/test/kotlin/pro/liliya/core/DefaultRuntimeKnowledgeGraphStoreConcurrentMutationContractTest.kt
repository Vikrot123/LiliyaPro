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
import pro.liliya.core.runtime.intelligence.knowledge.association.RuntimeKnowledgeAssociationType
import pro.liliya.core.runtime.intelligence.knowledge.graph.DefaultRuntimeKnowledgeGraphBuilder
import pro.liliya.core.runtime.intelligence.knowledge.graph.store.DefaultRuntimeKnowledgeGraphStore

class DefaultRuntimeKnowledgeGraphStoreConcurrentMutationContractTest {

    @Test
    fun concurrent_appends_must_preserve_every_edge() {

        repeat(100) { round ->

            val store =
                DefaultRuntimeKnowledgeGraphStore()

            val writers = 4
            val recordsPerWriter = 2_000

            val ready =
                CountDownLatch(writers)

            val start =
                CountDownLatch(1)

            val failure =
                AtomicReference<Throwable?>(null)

            val threads =
                List(writers) { writer ->

                    thread {
                        try {
                            ready.countDown()

                            assertTrue(
                                start.await(
                                    5,
                                    TimeUnit.SECONDS
                                )
                            )

                            repeat(recordsPerWriter) { index ->

                                val id =
                                    writer * recordsPerWriter + index

                                val source =
                                    RuntimeKnowledge(
                                        statement =
                                            "round-$round-source-$id",
                                        confidence = 0.9,
                                        source =
                                            RuntimeKnowledgeSource.EXPERIENCE,
                                        createdAt =
                                            id.toLong()
                                    )

                                val target =
                                    RuntimeKnowledge(
                                        statement =
                                            "round-$round-target-$id",
                                        confidence = 0.9,
                                        source =
                                            RuntimeKnowledgeSource.EXPERIENCE,
                                        createdAt =
                                            (id + 1).toLong()
                                    )

                                store.append(
                                    DefaultRuntimeKnowledgeGraphBuilder()
                                        .connect(
                                            source,
                                            target,
                                            RuntimeKnowledgeAssociationType.RELATED
                                        )
                                )
                            }
                        } catch (error: Throwable) {
                            failure.compareAndSet(
                                null,
                                error
                            )
                        }
                    }
                }

            assertTrue(
                ready.await(
                    5,
                    TimeUnit.SECONDS
                )
            )

            start.countDown()

            threads.forEach {
                it.join(10_000)
            }

            threads.forEachIndexed { index, writer ->
                assertTrue(
                    !writer.isAlive,
                    "graph writer $index must finish"
                )
            }

            assertNull(
                failure.get(),
                "concurrent graph append must not fail"
            )

            assertEquals(
                writers * recordsPerWriter,
                store.edges().size,
                "every successful concurrent graph append must be preserved"
            )
        }
    }
}
