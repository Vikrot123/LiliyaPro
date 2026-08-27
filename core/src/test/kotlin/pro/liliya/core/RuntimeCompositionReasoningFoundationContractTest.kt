package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.reasoning.RuntimeReasoningState

class RuntimeCompositionReasoningFoundationContractTest {

    @Test
    fun composition_owns_stable_reasoning_analyzer() {
        val composition =
            DefaultRuntimeComposition()

        assertSame(
            composition.reasoningAnalyzer(),
            composition.reasoningAnalyzer()
        )
    }

    @Test
    fun separate_compositions_own_independent_reasoning_analyzers() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.reasoningAnalyzer(),
            second.reasoningAnalyzer()
        )
    }

    @Test
    fun composition_forms_goal_plan_reasoning_chain() {
        val composition =
            DefaultRuntimeComposition()

        val intelligence =
            RuntimeIntelligenceFixture.result(
                significance =
                    RuntimeMeaningSignificance.CRITICAL,
                confidence = 0.95
            )

        val goal =
            composition
                .goalDeriver()
                .derive(intelligence)

        val plan =
            composition
                .planner()
                .plan(goal)

        val assessment =
            composition
                .reasoningAnalyzer()
                .analyze(
                    goal,
                    plan
                )

        assertEquals(
            RuntimeReasoningState.COHERENT,
            assessment.state
        )

        assertTrue(
            assessment.actionable
        )
    }

    @Test
    fun prepare_runtime_preserves_stateless_reasoning_owner() {
        val composition =
            DefaultRuntimeComposition()

        val analyzer =
            composition.reasoningAnalyzer()

        composition.prepareRuntime()

        assertSame(
            analyzer,
            composition.reasoningAnalyzer()
        )
    }
}
