package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionRecoveryPipelineIsolationContractTest {

    @Test
    fun separate_runtime_compositions_do_not_receive_each_other_recovery_events() {
        RuntimeEventBus.clear()

        val compositionA =
            DefaultRuntimeComposition()

        val compositionB =
            DefaultRuntimeComposition()

        val recoveredA = mutableListOf<String>()

        RuntimeEventBus.subscribe { event ->
            if (event is RuntimeEvent.RuntimeServiceRecovered) {
                recoveredA += event.serviceName
            }
        }

        compositionA.prepareRuntime()
        compositionB.prepareRuntime()

        compositionA.installRuntimeRecoveryEventBridge()
        compositionB.installRuntimeRecoveryEventBridge()

        val serviceA = TestService("service-a")
        val serviceB = TestService("service-b")

        compositionA.runtimeServiceRegistry()
            .register(serviceA)

        compositionB.runtimeServiceRegistry()
            .register(serviceB)

        compositionA.startRuntime()
        compositionB.startRuntime()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "service-a",
                reason = "composition-a failure",
                sourceRegistry = compositionA.runtimeServiceRegistry()
            )
        )

        assertEquals(
            RuntimeServiceState.RUNNING,
            serviceA.state
        )

        assertEquals(
            RuntimeServiceState.RUNNING,
            serviceB.state
        )

        assertEquals(
            listOf("service-a"),
            recoveredA
        )

        assertEquals(
            RuntimeServiceState.RUNNING,
            serviceB.state
        )

        compositionA.stopRuntime()
        compositionB.stopRuntime()

        RuntimeEventBus.clear()
    }


    private class TestService(
        override val name: String
    ) : RuntimeService {

        override var state =
            RuntimeServiceState.CREATED

        override fun start() {
            state = RuntimeServiceState.RUNNING
        }

        override fun stop() {
            state = RuntimeServiceState.STOPPED
        }
    }
}
