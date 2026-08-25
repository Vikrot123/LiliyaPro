package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertTrue

import pro.liliya.core.module.ModuleState
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType

class RuntimeCompositionCognitiveContextRecoverySnapshotRefreshContractTest {

    @Test
    fun successful_recovery_should_refresh_complete_cognitive_runtime_snapshot() {
        val composition = DefaultRuntimeComposition()

        val service = TestService(
            name = "recovery-refresh-service",
            state = RuntimeServiceState.RUNNING
        )

        composition.setRuntimeState(CoreRuntimeState.RUNNING)
        composition.setModuleStates(
            mapOf(
                "alpha" to ModuleState.RUNNING
            )
        )
        composition.runtimeServiceRegistry().register(service)

        composition.setFailureReason(null)

        val beforeFailure = snapshot(composition)

        composition.setRuntimeState(CoreRuntimeState.FAILED)
        composition.setModuleStates(
            mapOf(
                "alpha" to ModuleState.FAILED
            )
        )
        composition.setFailureReason("refresh-failure")
        service.state = RuntimeServiceState.FAILED

        val failed = snapshot(composition)

        composition.setRuntimeState(CoreRuntimeState.RUNNING)
        composition.setModuleStates(
            mapOf(
                "alpha" to ModuleState.RUNNING
            )
        )
        composition.setFailureReason(null)
        service.state = RuntimeServiceState.RUNNING

        val recovered = snapshot(composition)

        assertEquals(
            CoreRuntimeState.RUNNING.name,
            beforeFailure["runtimeState"]
        )
        assertEquals(
            CoreRuntimeState.FAILED.name,
            failed["runtimeState"]
        )
        assertEquals(
            CoreRuntimeState.RUNNING.name,
            recovered["runtimeState"]
        )

        assertEquals(
            mapOf("alpha" to ModuleState.RUNNING),
            beforeFailure["moduleStates"]
        )
        assertEquals(
            mapOf("alpha" to ModuleState.FAILED),
            failed["moduleStates"]
        )
        assertEquals(
            mapOf("alpha" to ModuleState.RUNNING),
            recovered["moduleStates"]
        )

        assertEquals(
            listOf(service.name),
            beforeFailure["activeServices"]
        )
        assertEquals(
            emptyList<String>(),
            failed["activeServices"]
        )
        assertEquals(
            listOf(service.name),
            recovered["activeServices"]
        )

        assertNull(beforeFailure["failureReason"])
        assertEquals(
            "refresh-failure",
            failed["failureReason"]
        )
        assertNull(recovered["failureReason"])

        val beforeTimestamp =
            beforeFailure["runtimeTimestamp"] as? Long

        val failedTimestamp =
            failed["runtimeTimestamp"] as? Long

        val recoveredTimestamp =
            recovered["runtimeTimestamp"] as? Long

        assertNotNull(beforeTimestamp)
        assertNotNull(failedTimestamp)
        assertNotNull(recoveredTimestamp)

        assertTrue(
            failedTimestamp >= beforeTimestamp
        )
        assertTrue(
            recoveredTimestamp >= failedTimestamp
        )
    }

    @Test
    fun failed_snapshot_should_remain_stable_after_recovery_refresh() {
        val composition = DefaultRuntimeComposition()

        val service = TestService(
            name = "stable-recovery-service",
            state = RuntimeServiceState.RUNNING
        )

        composition.setRuntimeState(CoreRuntimeState.RUNNING)
        composition.setModuleStates(
            mapOf(
                "alpha" to ModuleState.RUNNING
            )
        )
        composition.runtimeServiceRegistry().register(service)

        val failedPreparation = snapshotObject(composition)

        composition.setRuntimeState(CoreRuntimeState.FAILED)
        composition.setModuleStates(
            mapOf(
                "alpha" to ModuleState.FAILED
            )
        )
        composition.setFailureReason("stable-failure")
        service.state = RuntimeServiceState.FAILED

        val failed = snapshotObject(composition)

        val failedRuntimeState =
            failed.values["runtimeState"]

        val failedModuleStates =
            failed.values["moduleStates"]

        val failedActiveServices =
            failed.values["activeServices"]

        val failedReason =
            failed.values["failureReason"]

        composition.setRuntimeState(CoreRuntimeState.RUNNING)
        composition.setModuleStates(
            mapOf(
                "alpha" to ModuleState.RUNNING
            )
        )
        composition.setFailureReason(null)
        service.state = RuntimeServiceState.RUNNING

        val recovered = snapshotObject(composition)

        assertNotSame(
            failed.values,
            recovered.values
        )

        assertEquals(
            CoreRuntimeState.FAILED.name,
            failed.values["runtimeState"]
        )
        assertEquals(
            failedRuntimeState,
            failed.values["runtimeState"]
        )
        assertEquals(
            failedModuleStates,
            failed.values["moduleStates"]
        )
        assertEquals(
            failedActiveServices,
            failed.values["activeServices"]
        )
        assertEquals(
            failedReason,
            failed.values["failureReason"]
        )

        assertEquals(
            CoreRuntimeState.RUNNING.name,
            recovered.values["runtimeState"]
        )
        assertEquals(
            mapOf("alpha" to ModuleState.RUNNING),
            recovered.values["moduleStates"]
        )
        assertEquals(
            listOf(service.name),
            recovered.values["activeServices"]
        )
        assertNull(
            recovered.values["failureReason"]
        )

        assertEquals(
            CoreRuntimeState.RUNNING.name,
            failedPreparation.values["runtimeState"]
        )
    }

    @Test
    fun recovery_snapshot_should_use_current_working_context_type() {
        val composition = DefaultRuntimeComposition()

        composition.setRuntimeState(CoreRuntimeState.FAILED)
        composition.setModuleStates(
            mapOf(
                "alpha" to ModuleState.FAILED
            )
        )
        composition.setFailureReason("context-type-failure")

        val failed = snapshotObject(composition)

        composition.setRuntimeState(CoreRuntimeState.RUNNING)
        composition.setModuleStates(
            mapOf(
                "alpha" to ModuleState.RUNNING
            )
        )
        composition.setFailureReason(null)

        val recovered = snapshotObject(composition)

        assertEquals(
            CognitiveContextType.WORKING,
            failed.type
        )
        assertEquals(
            CognitiveContextType.WORKING,
            recovered.type
        )

        assertEquals(
            CoreRuntimeState.FAILED.name,
            failed.values["runtimeState"]
        )
        assertEquals(
            CoreRuntimeState.RUNNING.name,
            recovered.values["runtimeState"]
        )
    }

    private fun snapshot(
        composition: DefaultRuntimeComposition
    ): Map<String, Any?> {
        return snapshotObject(composition).values
    }

    private fun snapshotObject(
        composition: DefaultRuntimeComposition
    ): CognitiveContextSnapshot {
        val result = composition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.WORKING)

        return result.values["source_0"] as? CognitiveContextSnapshot
            ?: error("Runtime cognitive context snapshot not found")
    }

    private class TestService(
        override val name: String,
        override var state: RuntimeServiceState
    ) : RuntimeService {

        override fun start() {
            state = RuntimeServiceState.RUNNING
        }

        override fun stop() {
            state = RuntimeServiceState.STOPPED
        }
    }
}
