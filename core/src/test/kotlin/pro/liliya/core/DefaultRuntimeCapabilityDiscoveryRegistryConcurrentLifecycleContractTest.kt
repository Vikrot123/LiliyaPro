package pro.liliya.core

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue
import pro.liliya.core.module.LiliyaModule
import pro.liliya.core.module.ModuleDescriptor
import pro.liliya.core.module.ModuleState
import pro.liliya.core.runtime.capability.DefaultRuntimeCapabilityDiscoveryRegistry
import pro.liliya.core.runtime.capability.RuntimeCapabilityDiscovery
import pro.liliya.core.runtime.capability.RuntimeCapabilityProvider

class DefaultRuntimeCapabilityDiscoveryRegistryConcurrentLifecycleContractTest {

    @Test
    fun concurrent_discover_and_registration_are_safe() {

        val registry =
            DefaultRuntimeCapabilityDiscoveryRegistry()

        val module =
            object : LiliyaModule {
                override val descriptor =
                    ModuleDescriptor(
                        name = "ConcurrentDiscoveryModule",
                        version = "1.0",
                        critical = false
                    )

                override var state =
                    ModuleState.CREATED

                override fun init() {}
                override fun start() {}
                override fun stop() {}
            }

        val discoveries =
            List(256) {
                object : RuntimeCapabilityDiscovery {
                    override fun discover(
                        module: LiliyaModule
                    ): RuntimeCapabilityProvider? {
                        return null
                    }
                }
            }

        discoveries.forEach {
            registry.register(it)
        }

        val start = CountDownLatch(1)
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
                        registry.discover(module)
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
                        registry.register(
                            discoveries[
                                index % discoveries.size
                            ]
                        )
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

        assertTrue(!reader.isAlive)
        assertTrue(!writer.isAlive)

        assertNull(
            failure.get(),
            "capability discovery registry must tolerate concurrent discover/register"
        )
    }
}
