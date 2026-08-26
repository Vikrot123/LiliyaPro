package pro.liliya.core

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue
import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.action.RuntimeActionResult
import pro.liliya.core.runtime.dispatcher.RuntimeActionHandler
import pro.liliya.core.runtime.dispatcher.RuntimeActionHandlerRegistry

class RuntimeActionHandlerRegistryConcurrentLifecycleContractTest {

    @Test
    fun concurrent_find_snapshot_and_registration_are_safe() {

        val registry =
            RuntimeActionHandlerRegistry()

        val handlers =
            List(256) {
                object : RuntimeActionHandler {

                    override fun supports(
                        request: RuntimeActionRequest
                    ): Boolean {
                        return false
                    }

                    override fun handle(
                        request: RuntimeActionRequest
                    ): RuntimeActionResult {
                        throw UnsupportedOperationException()
                    }
                }
            }

        handlers.forEach {
            registry.register(it)
        }

        val start =
            CountDownLatch(1)

        val failure =
            AtomicReference<Throwable?>(null)

        val reader =
            thread {
                try {
                    assertTrue(
                        start.await(
                            5,
                            TimeUnit.SECONDS
                        )
                    )

                    repeat(20_000) {
                        registry.find { false }
                        registry.snapshot()
                    }
                } catch (error: Throwable) {
                    failure.compareAndSet(
                        null,
                        error
                    )
                }
            }

        val writer =
            thread {
                try {
                    start.countDown()

                    repeat(20_000) { index ->
                        registry.clear()

                        registry.register(
                            handlers[
                                index % handlers.size
                            ]
                        )

                        handlers.forEach {
                            registry.register(it)
                        }
                    }
                } catch (error: Throwable) {
                    failure.compareAndSet(
                        null,
                        error
                    )
                }
            }

        reader.join(15_000)
        writer.join(15_000)

        assertTrue(
            !reader.isAlive,
            "reader must finish"
        )

        assertTrue(
            !writer.isAlive,
            "writer must finish"
        )

        assertNull(
            failure.get(),
            "action handler registry must tolerate concurrent reads and mutation"
        )
    }
}
