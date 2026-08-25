package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType

class RuntimeCompositionCognitiveContextRealServiceRecoveryContractTest {

    @Test
    fun real_service_recovery_should_refresh_cognitive_context_active_services() {
        RuntimeEventBus.clear()

        val composition = DefaultRuntimeComposition()
        val service = RecoverableService()

        composition.prepareRuntime()
        composition.installRuntimeRecoveryEventBridge()
        composition.runtimeServiceRegistry().register(service)
        composition.startRuntime()

        assertEquals(
            RuntimeServiceState.RUNNING,
            service.state
        )

        val beforeFailure = snapshot(composition)

        assertEquals(
            listOf(service.name),
            beforeFailure["activeServices"]
        )

        service.stop()

        assertEquals(
            RuntimeServiceState.STOPPED,
            service.state
        )

        val failed = snapshot(composition)

        assertEquals(
            emptyList<String>(),
            failed["activeServices"]
        )

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = service.name,
                reason = "real-service-recovery"
            )
        )

        assertEquals(
            1,
            composition.runtimeSupervisor()
                .getRestartCount(service.name)
        )

        assertEquals(
            RuntimeServiceState.RUNNING,
            service.state
        )

        val recovered = snapshot(composition)

        assertEquals(
            listOf(service.name),
            recovered["activeServices"]
        )

        RuntimeEventBus.clear()
    }

    private fun snapshot(
        composition: DefaultRuntimeComposition
    ): Map<String, Any?> {
        val result = composition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.WORKING)

        val snapshot =
            result.values["source_0"] as? CognitiveContextSnapshot
                ?: error("Runtime cognitive context snapshot not found")

        return snapshot.values
    }

    private class RecoverableService : RuntimeService {

        override val name = "cognitive-real-recovery-service"

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
