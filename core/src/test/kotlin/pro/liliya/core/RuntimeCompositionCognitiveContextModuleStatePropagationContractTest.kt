package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

import pro.liliya.core.module.ModuleState
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionCognitiveContextModuleStatePropagationContractTest {

    @Test
    fun module_states_should_propagate_into_cognitive_context() {
        val composition = DefaultRuntimeComposition()

        val states = mapOf(
            "alpha" to ModuleState.RUNNING,
            "beta" to ModuleState.STOPPED
        )

        composition.setModuleStates(states)

        val result = composition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.TASK)

        val runtimeSnapshot =
            result.values["source_0"] as? CognitiveContextSnapshot

        assertNotNull(runtimeSnapshot)

        assertEquals(
            states,
            runtimeSnapshot.values["moduleStates"]
        )
    }

    @Test
    fun module_state_change_should_be_visible_on_following_process() {
        val composition = DefaultRuntimeComposition()

        composition.setModuleStates(
            mapOf(
                "alpha" to ModuleState.INITIALIZED
            )
        )

        val first = composition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.TASK)

        composition.setModuleStates(
            mapOf(
                "alpha" to ModuleState.RUNNING,
                "beta" to ModuleState.CREATED
            )
        )

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
            mapOf(
                "alpha" to ModuleState.INITIALIZED
            ),
            firstSnapshot.values["moduleStates"]
        )

        assertEquals(
            mapOf(
                "alpha" to ModuleState.RUNNING,
                "beta" to ModuleState.CREATED
            ),
            secondSnapshot.values["moduleStates"]
        )
    }

    @Test
    fun reset_runtime_state_should_clear_module_states_from_cognitive_context() {
        val composition = DefaultRuntimeComposition()

        composition.setModuleStates(
            mapOf(
                "alpha" to ModuleState.RUNNING,
                "beta" to ModuleState.FAILED
            )
        )

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
            mapOf(
                "alpha" to ModuleState.RUNNING,
                "beta" to ModuleState.FAILED
            ),
            beforeSnapshot.values["moduleStates"]
        )

        assertEquals(
            emptyMap<String, ModuleState>(),
            afterSnapshot.values["moduleStates"]
        )
    }
}
