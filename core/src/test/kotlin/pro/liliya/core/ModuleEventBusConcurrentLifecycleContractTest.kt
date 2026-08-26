package pro.liliya.core

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ModuleEventBusConcurrentLifecycleContractTest {

    @Test
    fun concurrent_publish_and_listener_lifecycle_are_safe() {

        ModuleEventBus.clear()

        val listeners =
            List(256) {
                { _: ModuleEvent ->
                    // no-op
                }
            }

        listeners.forEach {
            ModuleEventBus.subscribe(it)
        }

        val start = CountDownLatch(1)

        val failure =
            AtomicReference<Throwable?>(null)

        val publisher =
            thread {
                try {
                    assertTrue(
                        start.await(
                            5,
                            TimeUnit.SECONDS
                        )
                    )

                    repeat(5_000) {
                        ModuleEventBus.publish(
                            ModuleEvent.Failed(
                                moduleName = "concurrent-module",
                                phase = "TEST",
                                reason = "failure"
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

        val lifecycle =
            thread {
                try {
                    start.countDown()

                    repeat(20_000) { index ->
                        val listener =
                            listeners[
                                index % listeners.size
                            ]

                        ModuleEventBus.unsubscribe(
                            listener
                        )

                        ModuleEventBus.subscribe(
                            listener
                        )
                    }
                } catch (error: Throwable) {
                    failure.compareAndSet(
                        null,
                        error
                    )
                }
            }

        publisher.join(15_000)
        lifecycle.join(15_000)

        try {
            assertTrue(
                !publisher.isAlive,
                "publisher must finish"
            )

            assertTrue(
                !lifecycle.isAlive,
                "listener lifecycle must finish"
            )

            assertNull(
                failure.get(),
                "ModuleEventBus must tolerate concurrent snapshot and listener mutation"
            )
        } finally {
            ModuleEventBus.clear()
        }
    }
}
