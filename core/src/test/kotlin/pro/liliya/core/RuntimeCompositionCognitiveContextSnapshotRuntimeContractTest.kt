package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType

class RuntimeCompositionCognitiveContextSnapshotRuntimeContractTest {

    @Test
    fun snapshot_should_expose_current_runtime_state() {
        val composition = DefaultRuntimeComposition()

        composition.prepareRuntime()
        composition.startRuntime()

        val snapshot = snapshot(composition)

        assertEquals(
            CoreRuntimeState.RUNNING.name,
            snapshot["runtimeState"]
        )

        composition.stopRuntime()
    }

    @Test
    fun snapshot_should_expose_active_runtime_services() {
        val composition = DefaultRuntimeComposition()
        val service = TestService()

        composition.prepareRuntime()
        composition.runtimeServiceRegistry().register(service)
        composition.startRuntime()

        assertEquals(
            RuntimeServiceState.RUNNING,
            service.state
        )

        val snapshot = snapshot(composition)

        assertTrue(
            (snapshot["activeServices"] as List<*>)
                .contains(service.name)
        )

        composition.stopRuntime()
    }

    @Test
    fun snapshot_should_expose_runtime_failure_state() {
        val composition = DefaultRuntimeComposition()

        composition.prepareRuntime()
        composition.startRuntime()
        composition.markRuntimeFailed("snapshot-failure")

        val snapshot = snapshot(composition)

        assertEquals(
            CoreRuntimeState.FAILED.name,
            snapshot["runtimeState"]
        )

        assertEquals(
            "snapshot-failure",
            snapshot["failureReason"]
        )

        composition.stopRuntime()
    }

    @Test
    fun snapshot_should_reflect_runtime_recovery() {
        val composition = DefaultRuntimeComposition()

        composition.prepareRuntime()
        composition.startRuntime()
        composition.markRuntimeFailed("temporary-failure")

        composition.setFailureReason(null)
        composition.setRuntimeState(CoreRuntimeState.RUNNING)
        composition.markRuntimeRecovered()

        val snapshot = snapshot(composition)

        assertEquals(
            CoreRuntimeState.RUNNING.name,
            snapshot["runtimeState"]
        )

        assertEquals(
            null,
            snapshot["failureReason"]
        )

        composition.stopRuntime()
    }

    @Test
    fun separate_compositions_should_keep_runtime_snapshots_isolated() {
        val first = DefaultRuntimeComposition()
        val second = DefaultRuntimeComposition()

        first.prepareRuntime()
        second.prepareRuntime()

        first.startRuntime()

        val firstSnapshot = snapshot(first)
        val secondSnapshot = snapshot(second)

        assertEquals(
            CoreRuntimeState.RUNNING.name,
            firstSnapshot["runtimeState"]
        )

        assertEquals(
            CoreRuntimeState.STOPPED.name,
            secondSnapshot["runtimeState"]
        )

        first.stopRuntime()
        second.stopRuntime()
    }

    private fun snapshot(
        composition: DefaultRuntimeComposition
    ): Map<String, Any?> {
        return composition
            .cognitiveContextComposition()
            .service()
            .snapshot(CognitiveContextType.WORKING)
            .values
    }

    private class TestService : RuntimeService {
        override val name = "snapshot-runtime-service"

        override var state = RuntimeServiceState.CREATED

        override fun start() {
            state = RuntimeServiceState.RUNNING
        }

        override fun stop() {
            state = RuntimeServiceState.STOPPED
        }
    }
}
