package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals

import pro.liliya.core.module.ModuleState
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType

class RuntimeCompositionCognitiveContextModuleStateContractTest {

    @Test
    fun module_states_should_flow_into_cognitive_context() {
        val composition = DefaultRuntimeComposition()

        val states = mapOf(
            "CORE" to ModuleState.RUNNING,
            "MEMORY" to ModuleState.INITIALIZED,
            "FAILED_MODULE" to ModuleState.FAILED
        )

        composition.setModuleStates(states)

        assertEquals(
            states,
            moduleStates(composition)
        )
    }

    private fun moduleStates(
        composition: DefaultRuntimeComposition
    ): Map<String, ModuleState> {
        val result = composition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.WORKING)

        val snapshot = result.values["source_0"] as? CognitiveContextSnapshot
            ?: error("Runtime cognitive context snapshot not found")

        @Suppress("UNCHECKED_CAST")
        return snapshot.values["moduleStates"] as Map<String, ModuleState>
    }
}
