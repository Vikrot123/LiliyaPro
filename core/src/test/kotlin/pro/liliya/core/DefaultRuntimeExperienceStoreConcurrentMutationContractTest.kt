package pro.liliya.core

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperience
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceImportance
import pro.liliya.core.runtime.intelligence.experience.store.DefaultRuntimeExperienceStore
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class DefaultRuntimeExperienceStoreConcurrentMutationContractTest {

    @Test
    fun concurrent_appends_must_preserve_every_experience() {

        repeat(100) { round ->

            val store =
                DefaultRuntimeExperienceStore()

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

                                store.append(
                                    RuntimeExperience(
                                        description =
                                            "round-$round-writer-$writer-experience-$index",
                                        meaning =
                                            RuntimeMeaningResult(
                                                interpretation =
                                                    "experience-$id",
                                                confidence = 1.0,
                                                significance =
                                                    RuntimeMeaningSignificance.STABLE,
                                                generatedAt =
                                                    id.toLong()
                                            ),
                                        importance =
                                            RuntimeExperienceImportance.LOW,
                                        createdAt =
                                            id.toLong()
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
                    "experience writer $index must finish"
                )
            }

            assertNull(
                failure.get(),
                "concurrent experience append must not fail"
            )

            assertEquals(
                writers * recordsPerWriter,
                store.experiences().size,
                "every successful concurrent experience append must be preserved"
            )
        }
    }
}
