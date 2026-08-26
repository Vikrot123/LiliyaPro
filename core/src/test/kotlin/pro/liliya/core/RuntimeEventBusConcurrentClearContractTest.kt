package pro.liliya.core

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RuntimeEventBusConcurrentClearContractTest {

    @Test
    fun concurrent_publish_and_clear_are_safe() {

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

                    repeat(10_000) {
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

        val clearer =
            thread {
                try {
                    start.countDown()

                    repeat(10_000) {
                        RuntimeEventBus.clear()

                        listeners.forEach { listener ->
                            RuntimeEventBus.subscribe(
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
                "RuntimeEventBus must tolerate concurrent publish and clear"
            )
        } finally {
            RuntimeEventBus.clear()
        }
    }
}
