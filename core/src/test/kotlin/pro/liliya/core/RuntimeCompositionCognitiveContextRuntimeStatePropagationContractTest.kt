package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

import pro.liliya.core.CoreRuntimeState
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionCognitiveContextRuntimeStatePropagationContractTest {

    @Test
    fun runtime_state_should_propagate_into_cognitive_context() {
        val composition = DefaultRuntimeComposition()

        composition.setRuntimeState(CoreRuntimeState.RUNNING)

        val result = composition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.TASK)

        val runtimeSnapshot =
            result.values["source_0"] as? CognitiveContextSnapshot

        assertNotNull(runtimeSnapshot)
        assertEquals(
            "RUNNING",
            runtimeSnapshot.values["runtimeState"]
        )
    }

    @Test
    fun failure_reason_should_propagate_into_cognitive_context() {
        val composition = DefaultRuntimeComposition()

        composition.setRuntimeState(CoreRuntimeState.FAILED)
        composition.setFailureReason("cognitive-test-failure")

        val result = composition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.TASK)

        val runtimeSnapshot =
            result.values["source_0"] as? CognitiveContextSnapshot

        assertNotNull(runtimeSnapshot)
        assertEquals(
            "FAILED",
            runtimeSnapshot.values["runtimeState"]
        )
        assertEquals(
            "cognitive-test-failure",
            runtimeSnapshot.values["failureReason"]
        )
    }

    @Test
    fun runtime_state_change_should_be_visible_on_following_process() {
        val composition = DefaultRuntimeComposition()

        composition.setRuntimeState(CoreRuntimeState.STARTING)

        val first = composition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.TASK)

        composition.setRuntimeState(CoreRuntimeState.RUNNING)

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
            "STARTING",
            firstSnapshot.values["runtimeState"]
        )

        assertEquals(
            "RUNNING",
            secondSnapshot.values["runtimeState"]
        )
    }

    @Test
    fun reset_runtime_state_should_be_visible_to_cognitive_context() {
        val composition = DefaultRuntimeComposition()

        composition.setRuntimeState(CoreRuntimeState.RUNNING)

        val beforeReset = composition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.TASK)

        composition.resetRuntimeState()

        val afterReset = composition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.TASK)

        val beforeSnapshot =
            beforeReset.values["source_0"] as? CognitiveContextSnapshot

        val afterSnapshot =
            afterReset.values["source_0"] as? CognitiveContextSnapshot

        assertNotNull(beforeSnapshot)
        assertNotNull(afterSnapshot)

        assertEquals(
            "RUNNING",
            beforeSnapshot.values["runtimeState"]
        )

        assertEquals(
            "STOPPED",
            afterSnapshot.values["runtimeState"]
        )
    }
}
