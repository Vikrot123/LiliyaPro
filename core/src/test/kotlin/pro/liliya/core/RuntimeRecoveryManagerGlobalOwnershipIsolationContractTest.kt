package pro.liliya.core

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import pro.liliya.core.runtime.RuntimeRecoveryManager
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceRegistry
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.RuntimeSupervisor

class RuntimeRecoveryManagerGlobalOwnershipIsolationContractTest {

    @Test
    fun same_service_name_recovers_independently_across_registries() {

        RuntimeEventBus.clear()

        val firstRecoveryStarted = CountDownLatch(1)
        val releaseFirstRecovery = CountDownLatch(1)

        val registryA = RuntimeServiceRegistry()
        val registryB = RuntimeServiceRegistry()

        registryA.register(
            object : RuntimeService {

                override val name = "shared-service"

                override var state =
                    RuntimeServiceState.CREATED

                override fun start() {
                    firstRecoveryStarted.countDown()

                    assertTrue(
                        releaseFirstRecovery.await(
                            5,
                            TimeUnit.SECONDS
                        )
                    )

                    state = RuntimeServiceState.RUNNING
                }

                override fun stop() {
                    state = RuntimeServiceState.STOPPED
                }
            }
        )

        registryB.register(
            object : RuntimeService {

                override val name = "shared-service"

                override var state =
                    RuntimeServiceState.CREATED

                override fun start() {
                    state = RuntimeServiceState.RUNNING
                }

                override fun stop() {
                    state = RuntimeServiceState.STOPPED
                }
            }
        )

        val supervisorA =
            RuntimeSupervisor(
                registryProvider = { registryA }
            )

        val supervisorB =
            RuntimeSupervisor(
                registryProvider = { registryB }
            )

        val managerA =
            RuntimeRecoveryManager(
                supervisorA,
                registryA
            )

        val managerB =
            RuntimeRecoveryManager(
                supervisorB,
                registryB
            )

        managerA.install()
        managerB.install()

        val firstThread =
            thread {
                RuntimeEventBus.publish(
                    RuntimeEvent.RuntimeServiceFailed(
                        serviceName = "shared-service",
                        reason = "registry-a-failure",
                        sourceRegistry = registryA
                    )
                )
            }

        try {
            assertTrue(
                firstRecoveryStarted.await(
                    5,
                    TimeUnit.SECONDS
                )
            )

            val secondThread =
                thread {
                    RuntimeEventBus.publish(
                        RuntimeEvent.RuntimeServiceFailed(
                            serviceName = "shared-service",
                            reason = "registry-b-failure",
                            sourceRegistry = registryB
                        )
                    )
                }

            secondThread.join(5000)

            assertTrue(
                !secondThread.isAlive,
                "registry B recovery must not block behind registry A"
            )

            assertEquals(
                1,
                supervisorB.getRestartCount("shared-service"),
                "same service name in a different registry must recover independently"
            )

        } finally {
            releaseFirstRecovery.countDown()
            firstThread.join(5000)

            managerA.uninstall()
            managerB.uninstall()

            RuntimeEventBus.clear()
        }
    }

    @Test
    fun reset_of_other_manager_must_not_clear_active_recovery_guard() {

        RuntimeEventBus.clear()

        val firstRecoveryStarted = CountDownLatch(1)
        val releaseFirstRecovery = CountDownLatch(1)
        val startCalls = AtomicInteger(0)

        val registryA = RuntimeServiceRegistry()
        val registryB = RuntimeServiceRegistry()

        registryA.register(
            object : RuntimeService {

                override val name = "guard-service"

                override var state =
                    RuntimeServiceState.CREATED

                override fun start() {

                    val call =
                        startCalls.incrementAndGet()

                    if (call == 1) {
                        firstRecoveryStarted.countDown()

                        assertTrue(
                            releaseFirstRecovery.await(
                                5,
                                TimeUnit.SECONDS
                            )
                        )
                    }

                    state = RuntimeServiceState.RUNNING
                }

                override fun stop() {
                    state = RuntimeServiceState.STOPPED
                }
            }
        )

        val supervisorA =
            RuntimeSupervisor(
                registryProvider = { registryA }
            )

        val supervisorB =
            RuntimeSupervisor(
                registryProvider = { registryB }
            )

        val managerA =
            RuntimeRecoveryManager(
                supervisorA,
                registryA
            )

        val managerB =
            RuntimeRecoveryManager(
                supervisorB,
                registryB
            )

        managerA.install()
        managerB.install()

        val firstThread =
            thread {
                RuntimeEventBus.publish(
                    RuntimeEvent.RuntimeServiceFailed(
                        serviceName = "guard-service",
                        reason = "first-failure",
                        sourceRegistry = registryA
                    )
                )
            }

        try {
            assertTrue(
                firstRecoveryStarted.await(
                    5,
                    TimeUnit.SECONDS
                )
            )

            managerB.reset()

            val duplicateThread =
                thread {
                    RuntimeEventBus.publish(
                        RuntimeEvent.RuntimeServiceFailed(
                            serviceName = "guard-service",
                            reason = "duplicate-failure",
                            sourceRegistry = registryA
                        )
                    )
                }

            duplicateThread.join(5000)

            assertTrue(
                !duplicateThread.isAlive,
                "duplicate recovery publication must finish"
            )

            assertEquals(
                1,
                startCalls.get(),
                "reset of another manager must not remove registry A active recovery guard"
            )

        } finally {
            releaseFirstRecovery.countDown()
            firstThread.join(5000)

            managerA.uninstall()
            managerB.uninstall()

            RuntimeEventBus.clear()
        }
    }
}
