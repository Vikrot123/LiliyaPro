package pro.liliya.core

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.reflection.RuntimeReflectionSnapshot
import pro.liliya.core.runtime.intelligence.reflection.history.DefaultRuntimeReflectionHistory

class DefaultRuntimeReflectionHistoryConcurrentMutationContractTest {

    @Test
    fun concurrent_records_must_preserve_every_snapshot() {

        repeat(100) { round ->

            val history =
                DefaultRuntimeReflectionHistory()

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

                                history.record(
                                    RuntimeReflectionSnapshot(
                                        summary =
                                            "round-$round-writer-$writer-record-$index",
                                        healthy =
                                            index % 2 == 0,
                                        analyzedAt =
                                            (
                                                writer * recordsPerWriter
                                                + index
                                            ).toLong()
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
                    "reflection writer $index must finish"
                )
            }

            assertNull(
                failure.get(),
                "concurrent reflection recording must not fail"
            )

            assertEquals(
                writers * recordsPerWriter,
                history.snapshots().size,
                "every successful concurrent record must be preserved"
            )
        }
    }
}
