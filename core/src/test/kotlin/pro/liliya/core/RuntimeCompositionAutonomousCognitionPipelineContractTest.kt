package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.strategy.RuntimeStrategyState

class RuntimeCompositionAutonomousCognitionPipelineContractTest {

    @Test
    fun composition_owns_stable_autonomous_cognition_pipeline() {
        val composition =
            DefaultRuntimeComposition()

        assertSame(
            composition.autonomousCognitionPipeline(),
            composition.autonomousCognitionPipeline()
        )
    }

    @Test
    fun separate_compositions_own_independent_cognition_pipelines() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.autonomousCognitionPipeline(),
            second.autonomousCognitionPipeline()
        )
    }

    @Test
    fun composition_cognition_pipeline_stitches_existing_autonomy_chain() {
        val composition =
            DefaultRuntimeComposition()

        val result =
            composition
                .autonomousCognitionPipeline()
                .process(
                    RuntimeIntelligenceFixture.result(
                        significance =
                            RuntimeMeaningSignificance.CRITICAL,
                        confidence = 0.95
                    )
                )

        assertEquals(
            RuntimeStrategyState.RESTORE,
            result.strategy.state
        )

        assertSame(
            result.reasoningResult,
            result.intent.reasoningResult
        )

        assertSame(
            result.intent,
            result.strategy.intent
        )
    }

    @Test
    fun prepare_runtime_preserves_stateless_cognition_pipeline_owner() {
        val composition =
            DefaultRuntimeComposition()

        val pipeline =
            composition
                .autonomousCognitionPipeline()

        composition.prepareRuntime()

        assertSame(
            pipeline,
            composition
                .autonomousCognitionPipeline()
        )
    }
}
