package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType

class RuntimeCompositionCognitiveContextFailureReasonContractTest {

    @Test
    fun runtime_failure_reason_should_flow_into_cognitive_context() {
        val composition = DefaultRuntimeComposition()

        composition.markRuntimeFailed("memory-service-failed")

        assertEquals(
            "memory-service-failed",
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
