package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionCognitiveContextFailureReasonPropagationContractTest {

    @Test
    fun failure_reason_should_propagate_into_cognitive_context() {
        val composition = DefaultRuntimeComposition()

        composition.setFailureReason("critical-runtime-failure")

        val result = composition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.TASK)

        val snapshot =
            result.values["source_0"] as? CognitiveContextSnapshot

        assertNotNull(snapshot)

        assertEquals(
            "critical-runtime-failure",
            snapshot.values["failureReason"]
        )
    }

    @Test
    fun failure_reason_change_should_be_visible_on_following_process() {
        val composition = DefaultRuntimeComposition()

        composition.setFailureReason("first-failure")

        val first = composition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.TASK)

        composition.setFailureReason("second-failure")

        val second = composition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.TASK)

        val firstSnapshot =
            first.values["source_0"] as? CognitiveContextSnapshot

        val secondSnapshot =
            second.values["source_0"] as? CognitiveContextSnapshot

        assertNotNull(firstSnapshot)
        assertNotNull(secondSnapshot)

        assertEquals(
            "first-failure",
            firstSnapshot.values["failureReason"]
        )

        assertEquals(
            "second-failure",
            secondSnapshot.values["failureReason"]
        )
    }

    @Test
    fun null_failure_reason_should_be_visible_on_following_process() {
        val composition = DefaultRuntimeComposition()

        composition.setFailureReason("temporary-failure")

        val failed = composition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.TASK)

        composition.setFailureReason(null)

        val recovered = composition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.TASK)

        val failedSnapshot =
            failed.values["source_0"] as? CognitiveContextSnapshot

        val recoveredSnapshot =
            recovered.values["source_0"] as? CognitiveContextSnapshot

        assertNotNull(failedSnapshot)
        assertNotNull(recoveredSnapshot)

        assertEquals(
            "temporary-failure",
            failedSnapshot.values["failureReason"]
        )

        assertEquals(
            null,
            recoveredSnapshot.values["failureReason"]
        )
    }

    @Test
    fun separate_compositions_should_keep_failure_reason_isolated() {
        val first = DefaultRuntimeComposition()
        val second = DefaultRuntimeComposition()

        first.setFailureReason("first-failure")
        second.setFailureReason("second-failure")

        val firstResult = first
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.TASK)

        val secondResult = second
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.TASK)

        val firstSnapshot =
            firstResult.values["source_0"] as? CognitiveContextSnapshot

        val secondSnapshot =
            secondResult.values["source_0"] as? CognitiveContextSnapshot

        assertNotNull(firstSnapshot)
        assertNotNull(secondSnapshot)

        assertEquals(
            "first-failure",
            firstSnapshot.values["failureReason"]
        )

        assertEquals(
            "second-failure",
            secondSnapshot.values["failureReason"]
        )
    }
}
