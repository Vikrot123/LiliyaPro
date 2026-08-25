package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType

class RuntimeCompositionCognitiveContextFullRuntimeRecoveryContractTest {

    @Test
    fun full_runtime_recovery_should_restore_running_cognitive_context() {
        val composition = DefaultRuntimeComposition()

        composition.prepareRuntime()
        composition.startRuntime()

        composition.markRuntimeFailed("full-runtime-failure")

        val failed = snapshot(composition)

        assertEquals(
            CoreRuntimeState.FAILED.name,
            failed["runtimeState"]
        )

        assertEquals(
            "full-runtime-failure",
            failed["failureReason"]
        )

        composition.setFailureReason(null)
        composition.setRuntimeState(CoreRuntimeState.RUNNING)
        composition.markRuntimeRecovered()

        val recovered = snapshot(composition)

        assertEquals(
            CoreRuntimeState.RUNNING.name,
            recovered["runtimeState"]
        )

        assertNull(
            recovered["failureReason"]
        )
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
}
