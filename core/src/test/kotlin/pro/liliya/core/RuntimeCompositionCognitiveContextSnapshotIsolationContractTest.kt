package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame

import pro.liliya.core.module.ModuleState
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionCognitiveContextSnapshotIsolationContractTest {

    @Test
    fun previous_snapshot_should_not_change_after_runtime_state_mutation() {
        val composition = DefaultRuntimeComposition()

        composition.setRuntimeState(CoreRuntimeState.RUNNING)
        composition.setModuleStates(
            mapOf("alpha" to ModuleState.RUNNING)
        )

        val alpha = TestService(
            name = "alpha",
            state = RuntimeServiceState.RUNNING
        )

        composition.runtimeServiceRegistry().register(alpha)

        val first = composition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.TASK)

        val firstSnapshot =
            first.values["source_0"] as? CognitiveContextSnapshot

        assertNotNull(firstSnapshot)

        composition.setRuntimeState(CoreRuntimeState.STOPPED)
        composition.setModuleStates(emptyMap())
        alpha.state = RuntimeServiceState.STOPPED

        val second = composition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.TASK)

        val secondSnapshot =
            second.values["source_0"] as? CognitiveContextSnapshot

        assertNotNull(secondSnapshot)

        assertEquals(
            CoreRuntimeState.RUNNING.name,
            firstSnapshot.values["runtimeState"]
        )

        assertEquals(
            mapOf("alpha" to ModuleState.RUNNING),
            firstSnapshot.values["moduleStates"]
        )

        assertEquals(
            listOf("alpha"),
            firstSnapshot.values["activeServices"]
        )

        assertEquals(
            CoreRuntimeState.STOPPED.name,
            secondSnapshot.values["runtimeState"]
        )

        assertEquals(
            emptyMap<String, ModuleState>(),
            secondSnapshot.values["moduleStates"]
        )

        assertEquals(
            emptyList<String>(),
            secondSnapshot.values["activeServices"]
        )
    }

    @Test
    fun consecutive_snapshots_should_have_independent_values_maps() {
        val composition = DefaultRuntimeComposition()

        val first = composition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.TASK)

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

        assertNotSame(
            firstSnapshot.values,
            secondSnapshot.values
        )
    }

    @Test
    fun separate_compositions_should_not_share_snapshot_values() {
        val firstComposition = DefaultRuntimeComposition()
        val secondComposition = DefaultRuntimeComposition()

        firstComposition.setRuntimeState(CoreRuntimeState.RUNNING)
        firstComposition.setModuleStates(
            mapOf("first" to ModuleState.RUNNING)
        )

        secondComposition.setRuntimeState(CoreRuntimeState.STOPPED)
        secondComposition.setModuleStates(
            mapOf("second" to ModuleState.STOPPED)
        )

        val firstResult = firstComposition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.TASK)

        val secondResult = secondComposition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.TASK)

        val firstSnapshot =
            firstResult.values["source_0"] as? CognitiveContextSnapshot

        val secondSnapshot =
            secondResult.values["source_0"] as? CognitiveContextSnapshot

        assertNotNull(firstSnapshot)
        assertNotNull(secondSnapshot)

        assertNotSame(
            firstSnapshot.values,
            secondSnapshot.values
        )

        assertEquals(
            CoreRuntimeState.RUNNING.name,
            firstSnapshot.values["runtimeState"]
        )

        assertEquals(
            CoreRuntimeState.STOPPED.name,
            secondSnapshot.values["runtimeState"]
        )
    }

    @Test
    fun active_service_snapshot_should_not_follow_later_service_mutation() {
        val composition = DefaultRuntimeComposition()

        val alpha = TestService(
            name = "alpha",
            state = RuntimeServiceState.RUNNING
        )

        composition.runtimeServiceRegistry().register(alpha)

        val first = composition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.WORKING)

        val firstSnapshot =
            first.values["source_0"] as? CognitiveContextSnapshot

        assertNotNull(firstSnapshot)

        alpha.state = RuntimeServiceState.STOPPED

        val second = composition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.WORKING)

        val secondSnapshot =
            second.values["source_0"] as? CognitiveContextSnapshot

        assertNotNull(secondSnapshot)

        assertEquals(
            listOf("alpha"),
            firstSnapshot.values["activeServices"]
        )

        assertEquals(
            emptyList<String>(),
            secondSnapshot.values["activeServices"]
        )
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
