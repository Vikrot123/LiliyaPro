package pro.liliya.core

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue
import pro.liliya.core.runtime.observer.DefaultRuntimeObserverRegistry
import pro.liliya.core.runtime.observer.RuntimeObserver

class DefaultRuntimeObserverRegistryConcurrentLifecycleContractTest {

    @Test
    fun concurrent_publish_and_observer_lifecycle_are_safe() {

        val registry =
            DefaultRuntimeObserverRegistry()

        val observers =
            List(256) {
                object : RuntimeObserver {
                    override fun onRuntimeEvent(
                        event: RuntimeEvent
                    ) {
                        // no-op
                    }
                }
            }

        observers.forEach {
            registry.subscribe(it)
        }

        val start =
            CountDownLatch(1)

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
                        registry.publish(
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
                        val observer =
                            observers[
                                index % observers.size
                            ]

                        registry.unsubscribe(
                            observer
                        )

                        registry.subscribe(
                            observer
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

        assertTrue(
            !publisher.isAlive,
            "publisher must finish"
        )

        assertTrue(
            !lifecycle.isAlive,
            "observer lifecycle must finish"
        )

        assertNull(
            failure.get(),
            "observer registry must tolerate concurrent publish and lifecycle mutation"
        )
    }
}
