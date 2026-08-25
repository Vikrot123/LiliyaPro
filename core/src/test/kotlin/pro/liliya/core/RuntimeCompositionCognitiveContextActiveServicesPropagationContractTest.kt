package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionCognitiveContextActiveServicesPropagationContractTest {

    @Test
    fun running_services_should_propagate_into_cognitive_context() {
        val composition = DefaultRuntimeComposition()

        composition.runtimeServiceRegistry().register(
            TestService(
                name = "alpha",
                state = RuntimeServiceState.RUNNING
            )
        )

        composition.runtimeServiceRegistry().register(
            TestService(
                name = "beta",
                state = RuntimeServiceState.STOPPED
            )
        )

        val result = composition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.TASK)

        val snapshot =
            result.values["source_0"] as? CognitiveContextSnapshot

        assertNotNull(snapshot)

        assertEquals(
            listOf("alpha"),
            snapshot.values["activeServices"]
        )
    }

    @Test
    fun service_state_change_should_be_visible_on_following_process() {
        val composition = DefaultRuntimeComposition()

        val alpha = TestService(
            name = "alpha",
            state = RuntimeServiceState.STOPPED
        )

        composition.runtimeServiceRegistry().register(alpha)

        val first = composition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.TASK)

        alpha.state = RuntimeServiceState.RUNNING

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
            emptyList<String>(),
            firstSnapshot.values["activeServices"]
        )

        assertEquals(
            listOf("alpha"),
            secondSnapshot.values["activeServices"]
        )
    }

    @Test
    fun separate_compositions_should_keep_active_services_isolated() {
        val first = DefaultRuntimeComposition()
        val second = DefaultRuntimeComposition()

        first.runtimeServiceRegistry().register(
            TestService(
                name = "first-only-service",
                state = RuntimeServiceState.RUNNING
            )
        )

        second.runtimeServiceRegistry().register(
            TestService(
                name = "second-only-service",
                state = RuntimeServiceState.RUNNING
            )
        )

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
            listOf("first-only-service"),
            firstSnapshot.values["activeServices"]
        )

        assertEquals(
            listOf("second-only-service"),
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
