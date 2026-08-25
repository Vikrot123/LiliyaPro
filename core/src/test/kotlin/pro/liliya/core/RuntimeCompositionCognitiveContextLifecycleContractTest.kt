package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType

class RuntimeCompositionCognitiveContextLifecycleContractTest {

    @Test
    fun runtime_lifecycle_state_should_flow_into_cognitive_context() {
        val composition = DefaultRuntimeComposition()

        assertEquals(
            CoreRuntimeState.STOPPED.name,
            cognitiveRuntimeState(composition)
        )

        composition.start()

        assertEquals(
            CoreRuntimeState.RUNNING.name,
            cognitiveRuntimeState(composition)
        )

        composition.stop()

        assertEquals(
            CoreRuntimeState.STOPPED.name,
            cognitiveRuntimeState(composition)
        )
    }

    private fun cognitiveRuntimeState(
        composition: DefaultRuntimeComposition
    ): String {
        val result = composition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.WORKING)

        val snapshot = result.values["source_0"] as? CognitiveContextSnapshot
            ?: error("Runtime cognitive context snapshot not found")

        return snapshot.values["runtimeState"] as String
    }
}
