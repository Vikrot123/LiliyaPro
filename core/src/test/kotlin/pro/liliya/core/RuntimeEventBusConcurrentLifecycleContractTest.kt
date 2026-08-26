package pro.liliya.core

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RuntimeEventBusConcurrentLifecycleContractTest {

    @Test
    fun concurrent_publish_and_listener_lifecycle_are_safe() {

        RuntimeEventBus.clear()

        val listeners =
            List(256) {
                { _: RuntimeEvent ->
                    // no-op
                }
            }

        listeners.forEach {
            RuntimeEventBus.subscribe(it)
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
                        RuntimeEventBus.publish(
                            RuntimeEvent.RuntimeReady
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

                        RuntimeEventBus.unsubscribe(
                            listener
                        )

                        RuntimeEventBus.subscribe(
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
                "RuntimeEventBus must tolerate concurrent snapshot and listener mutation"
            )
        } finally {
            RuntimeEventBus.clear()
        }
    }
}
