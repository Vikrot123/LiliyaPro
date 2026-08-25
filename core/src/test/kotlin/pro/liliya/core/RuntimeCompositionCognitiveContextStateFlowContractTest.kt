package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType

class RuntimeCompositionCognitiveContextStateFlowContractTest {

    @Test
    fun runtime_state_should_flow_into_cognitive_context() {
        val composition = DefaultRuntimeComposition()

        composition.setRuntimeState(CoreRuntimeState.RUNNING)

        val runningResult =
            composition
                .cognitiveContextComposition()
                .service()
                .process(CognitiveContextType.WORKING)

        val runningSnapshot =
            runningResult.values["source_0"] as? CognitiveContextSnapshot

        check(runningSnapshot != null)

        assertEquals(
            CoreRuntimeState.RUNNING.name,
            runningSnapshot.values["runtimeState"]
        )

        composition.setRuntimeState(CoreRuntimeState.FAILED)

        val failedResult =
            composition
                .cognitiveContextComposition()
                .service()
                .process(CognitiveContextType.WORKING)

        val failedSnapshot =
            failedResult.values["source_0"] as? CognitiveContextSnapshot

        check(failedSnapshot != null)

        assertEquals(
            CoreRuntimeState.FAILED.name,
            failedSnapshot.values["runtimeState"]
        )
    }
}
