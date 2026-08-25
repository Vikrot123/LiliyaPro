package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType

class RuntimeCompositionCognitiveContextFailureClearContractTest {

    @Test
    fun cleared_runtime_failure_reason_should_disappear_from_cognitive_context() {
        val composition = DefaultRuntimeComposition()

        composition.markRuntimeFailed("temporary-failure")

        assertEquals(
            "temporary-failure",
            failureReason(composition)
        )

        composition.setFailureReason(null)

        assertNull(
            failureReason(composition)
        )
    }

    private fun failureReason(
        composition: DefaultRuntimeComposition
    ): String? {
        val result = composition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.WORKING)

        val snapshot =
            result.values["source_0"] as? CognitiveContextSnapshot
                ?: error("Runtime cognitive context snapshot not found")

        return snapshot.values["failureReason"] as String?
    }
}
