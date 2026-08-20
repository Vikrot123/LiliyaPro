package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionRuntimeServiceBootstrapPrepareRecoveryListenerIsolationContractTest {

    @Test
    fun old_bootstrap_does_not_receive_recovery_events_after_prepare_runtime() {
        RuntimeEventBus.clear()

        val composition = DefaultRuntimeComposition()

        val service = object : RuntimeService {
            override val name = "stale-bootstrap-service"
            override var state = RuntimeServiceState.CREATED
                private set

            override fun start() {
                state = RuntimeServiceState.RUNNING
            }

            override fun stop() {
                state = RuntimeServiceState.STOPPED
            }
        }

        composition.registerRuntimeService(service)

        val oldBootstrap = composition.serviceBootstrap()
        oldBootstrap.start()

        composition.prepareRuntime()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = service.name,
                reason = "stale listener probe"
            )
        )

        assertEquals(
            0,
            oldBootstrap.getRestartCount(service.name)
        )

        assertEquals(
            emptyMap(),
            composition.serviceBootstrap().getStates()
        )

        RuntimeEventBus.clear()
    }
}
