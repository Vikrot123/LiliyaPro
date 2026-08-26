package pro.liliya.core

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ModuleEventBusConcurrentClearContractTest {

    @Test
    fun concurrent_publish_and_clear_are_safe() {

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

                    repeat(10_000) {
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

        val clearer =
            thread {
                try {
                    start.countDown()

                    repeat(10_000) {
                        ModuleEventBus.clear()

                        listeners.forEach { listener ->
                            ModuleEventBus.subscribe(
                                listener
                            )
                        }
                    }
                } catch (error: Throwable) {
                    failure.compareAndSet(
                        null,
                        error
                    )
                }
            }

        publisher.join(15_000)
        clearer.join(15_000)

        try {
            assertTrue(
                !publisher.isAlive,
                "publisher must finish"
            )

            assertTrue(
                !clearer.isAlive,
                "clear lifecycle must finish"
            )

            assertNull(
                failure.get(),
                "ModuleEventBus must tolerate concurrent publish and clear"
            )
        } finally {
            ModuleEventBus.clear()
        }
    }
}
