package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.goal.RuntimeGoalState
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.reasoning.RuntimeReasoningState

class RuntimeCompositionAutonomousReasoningPipelineContractTest {

    @Test
    fun composition_owns_stable_autonomous_reasoning_pipeline() {
        val composition =
            DefaultRuntimeComposition()

        assertSame(
            composition.autonomousReasoningPipeline(),
            composition.autonomousReasoningPipeline()
        )
    }

    @Test
    fun separate_compositions_own_independent_pipelines() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.autonomousReasoningPipeline(),
            second.autonomousReasoningPipeline()
        )
    }

    @Test
    fun composition_pipeline_forms_full_autonomous_reasoning_chain() {
        val composition =
            DefaultRuntimeComposition()

        val result =
            composition
                .autonomousReasoningPipeline()
                .process(
                    RuntimeIntelligenceFixture.result(
                        significance =
                            RuntimeMeaningSignificance.CRITICAL,
                        confidence = 0.95
                    )
                )

        assertEquals(
            RuntimeGoalState.RECOVER,
            result.goal.state
        )

        assertEquals(
            RuntimeReasoningState.COHERENT,
            result.reasoning.state
        )

        assertTrue(
            result.coherent
        )

        assertTrue(
            result.actionable
        )
    }

    @Test
    fun prepare_runtime_preserves_stateless_pipeline_owner() {
        val composition =
            DefaultRuntimeComposition()

        val pipeline =
            composition
                .autonomousReasoningPipeline()

        composition.prepareRuntime()

        assertSame(
            pipeline,
            composition
                .autonomousReasoningPipeline()
        )

        val result =
            pipeline.process(
                RuntimeIntelligenceFixture.result(
                    significance =
                        RuntimeMeaningSignificance.WARNING,
                    confidence = 0.80
                )
            )

        assertEquals(
            RuntimeGoalState.INVESTIGATE,
            result.goal.state
        )

        assertTrue(
            result.coherent
        )
    }
}
