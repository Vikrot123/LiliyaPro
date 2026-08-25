package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

import pro.liliya.core.module.ModuleState
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionCognitiveContextTypeRuntimePropagationContractTest {

    @Test
    fun task_context_should_receive_current_runtime_snapshot() {
        val composition = DefaultRuntimeComposition()

        composition.setRuntimeState(CoreRuntimeState.RUNNING)
        composition.setModuleStates(
            mapOf(
                "alpha" to ModuleState.RUNNING
            )
        )
        composition.runtimeServiceRegistry().register(
            TestService(
                name = "service-alpha",
                state = RuntimeServiceState.RUNNING
            )
        )
        composition.setFailureReason("task-context-failure")

        val result = composition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.TASK)

        assertRuntimeSnapshot(
            result.values["source_0"] as? CognitiveContextSnapshot,
            CognitiveContextType.TASK,
            "task-context-failure"
        )
    }

    @Test
    fun reasoning_context_should_receive_current_runtime_snapshot() {
        val composition = DefaultRuntimeComposition()

        composition.setRuntimeState(CoreRuntimeState.RUNNING)
        composition.setModuleStates(
            mapOf(
                "alpha" to ModuleState.RUNNING
            )
        )
        composition.runtimeServiceRegistry().register(
            TestService(
                name = "service-alpha",
                state = RuntimeServiceState.RUNNING
            )
        )
        composition.setFailureReason("reasoning-context-failure")

        val result = composition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.WORKING)

        assertRuntimeSnapshot(
            result.values["source_0"] as? CognitiveContextSnapshot,
            CognitiveContextType.WORKING,
            "reasoning-context-failure"
        )
    }

    @Test
    fun reflection_context_should_receive_current_runtime_snapshot() {
        val composition = DefaultRuntimeComposition()

        composition.setRuntimeState(CoreRuntimeState.RUNNING)
        composition.setModuleStates(
            mapOf(
                "alpha" to ModuleState.RUNNING
            )
        )
        composition.runtimeServiceRegistry().register(
            TestService(
                name = "service-alpha",
                state = RuntimeServiceState.RUNNING
            )
        )
        composition.setFailureReason("reflection-context-failure")

        val result = composition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.PROCESSOR)

        assertRuntimeSnapshot(
            result.values["source_0"] as? CognitiveContextSnapshot,
            CognitiveContextType.PROCESSOR,
            "reflection-context-failure"
        )
    }

    @Test
    fun context_type_change_should_not_reuse_stale_runtime_snapshot() {
        val composition = DefaultRuntimeComposition()

        composition.setRuntimeState(CoreRuntimeState.RUNNING)
        composition.setModuleStates(
            mapOf(
                "alpha" to ModuleState.RUNNING
            )
        )
        composition.runtimeServiceRegistry().register(
            TestService(
                name = "service-alpha",
                state = RuntimeServiceState.RUNNING
            )
        )
        composition.setFailureReason("first")

        val first = composition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.TASK)

        composition.setRuntimeState(CoreRuntimeState.STOPPED)
        composition.setModuleStates(emptyMap())
        composition.setFailureReason("second")

        val second = composition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.WORKING)

        val firstSnapshot =
            first.values["source_0"] as? CognitiveContextSnapshot

        val secondSnapshot =
            second.values["source_0"] as? CognitiveContextSnapshot

        assertNotNull(firstSnapshot)
        assertNotNull(secondSnapshot)

        assertEquals(
            CognitiveContextType.TASK,
            firstSnapshot.type
        )
        assertEquals(
            CognitiveContextType.WORKING,
            secondSnapshot.type
        )

        assertEquals(
            CoreRuntimeState.RUNNING.name,
            firstSnapshot.values["runtimeState"]
        )
        assertEquals(
            CoreRuntimeState.STOPPED.name,
            secondSnapshot.values["runtimeState"]
        )

        assertEquals(
            mapOf("alpha" to ModuleState.RUNNING),
            firstSnapshot.values["moduleStates"]
        )
        assertEquals(
            emptyMap<String, ModuleState>(),
            secondSnapshot.values["moduleStates"]
        )

        assertEquals(
            "first",
            firstSnapshot.values["failureReason"]
        )
        assertEquals(
            "second",
            secondSnapshot.values["failureReason"]
        )
    }

    private fun assertRuntimeSnapshot(
        snapshot: CognitiveContextSnapshot?,
        expectedType: CognitiveContextType,
        expectedFailureReason: String
    ) {
        assertNotNull(snapshot)

        assertEquals(
            expectedType,
            snapshot.type
        )

        assertEquals(
            CoreRuntimeState.RUNNING.name,
            snapshot.values["runtimeState"]
        )

        assertEquals(
            mapOf("alpha" to ModuleState.RUNNING),
            snapshot.values["moduleStates"]
        )

        assertEquals(
            listOf("service-alpha"),
            snapshot.values["activeServices"]
        )

        assertEquals(
            expectedFailureReason,
            snapshot.values["failureReason"]
        )

        val timestamp =
            snapshot.values["runtimeTimestamp"] as? Long

        assertNotNull(timestamp)
        assertTrue(timestamp > 0L)
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
