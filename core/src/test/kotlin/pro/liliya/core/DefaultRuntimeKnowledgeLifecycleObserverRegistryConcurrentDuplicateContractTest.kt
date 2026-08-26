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
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.observer.RuntimeKnowledgeLifecycleObserver
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.observer.registry.DefaultRuntimeKnowledgeLifecycleObserverRegistry
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.RuntimeKnowledgeLifecycleProcessingStatus
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.RuntimeKnowledgeLifecycleServiceResult

class DefaultRuntimeKnowledgeLifecycleObserverRegistryConcurrentDuplicateContractTest {

    @Test
    fun concurrent_duplicate_registration_must_deliver_once() {

        repeat(10_000) {

            val registry =
                DefaultRuntimeKnowledgeLifecycleObserverRegistry()

            val deliveries =
                AtomicInteger(0)

            val observer =
                object : RuntimeKnowledgeLifecycleObserver {

                    override fun onProcessed(
                        result: RuntimeKnowledgeLifecycleServiceResult
                    ) {
                        deliveries.incrementAndGet()
                    }
                }

            val start =
                CountDownLatch(1)

            val ready =
                CountDownLatch(2)

            val failure =
                AtomicReference<Throwable?>(null)

            val first =
                thread {
                    try {
                        ready.countDown()

                        assertTrue(
                            start.await(
                                5,
                                TimeUnit.SECONDS
                            )
                        )

                        registry.register(
                            observer
                        )
                    } catch (error: Throwable) {
                        failure.compareAndSet(
                            null,
                            error
                        )
                    }
                }

            val second =
                thread {
                    try {
                        ready.countDown()

                        assertTrue(
                            start.await(
                                5,
                                TimeUnit.SECONDS
                            )
                        )

                        registry.register(
                            observer
                        )
                    } catch (error: Throwable) {
                        failure.compareAndSet(
                            null,
                            error
                        )
                    }
                }

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
                "first registration thread must finish"
            )

            assertTrue(
                !second.isAlive,
                "second registration thread must finish"
            )

            assertNull(
                failure.get(),
                "concurrent registration must not fail"
            )

            registry.notify(
                RuntimeKnowledgeLifecycleServiceResult(
                    status =
                        RuntimeKnowledgeLifecycleProcessingStatus.EXECUTED,
                    pipelineResult = null,
                    error = null
                )
            )

            assertEquals(
                1,
                deliveries.get(),
                "concurrent duplicate registration must not create duplicate delivery"
            )
        }
    }
}
