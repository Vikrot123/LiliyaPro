package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.RuntimeEvent
import pro.liliya.core.RuntimeEventBus
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceProvider
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionRuntimeServiceBootstrapCrossCompositionRecoveryIsolationContractTest {

    @Test
    fun runtime_service_failure_must_not_trigger_recovery_in_another_composition() {
        RuntimeEventBus.clear()

        val first = DefaultRuntimeComposition()
        val second = DefaultRuntimeComposition()

        val firstService = RecoverableService("shared-service")
        val secondService = RecoverableService("shared-service")

        first.setRuntimeServiceProvider(providerOf(firstService))
        second.setRuntimeServiceProvider(providerOf(secondService))

        val firstBootstrap = first.serviceBootstrap()
        val secondBootstrap = second.serviceBootstrap()

        firstBootstrap.start()
        secondBootstrap.start()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "shared-service",
                reason = "first composition failure",
                sourceRegistry = first.runtimeServiceRegistry()
            )
        )

        assertEquals(
            1,
            firstBootstrap.getRestartCount("shared-service")
        )

        assertEquals(
            0,
            secondBootstrap.getRestartCount("shared-service")
        )

        secondBootstrap.stop()
        firstBootstrap.stop()
        RuntimeEventBus.clear()
    }

    private fun providerOf(
        service: RuntimeService
    ) = object : RuntimeServiceProvider {
        override fun provideServices(): List<RuntimeService> {
            return listOf(service)
        }
    }

    private class RecoverableService(
        override val name: String
    ) : RuntimeService {

        override var state = RuntimeServiceState.CREATED
            private set

        override fun start() {
            state = RuntimeServiceState.RUNNING
        }

        override fun stop() {
            state = RuntimeServiceState.STOPPED
        }
    }
}
