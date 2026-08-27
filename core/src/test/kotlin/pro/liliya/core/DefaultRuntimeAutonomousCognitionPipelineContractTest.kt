package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.cognition.DefaultRuntimeAutonomousCognitionPipeline
import pro.liliya.core.runtime.intelligence.goal.DefaultRuntimeGoalDeriver
import pro.liliya.core.runtime.intelligence.intent.DefaultRuntimeIntentDeriver
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.planning.DefaultRuntimePlanner
import pro.liliya.core.runtime.intelligence.reasoning.DefaultRuntimeReasoningAnalyzer
import pro.liliya.core.runtime.intelligence.reasoning.orchestration.DefaultRuntimeAutonomousReasoningPipeline
import pro.liliya.core.runtime.intelligence.strategy.DefaultRuntimeStrategyDeriver
import pro.liliya.core.runtime.intelligence.strategy.RuntimeStrategyState

class DefaultRuntimeAutonomousCognitionPipelineContractTest {

    private val pipeline =
        DefaultRuntimeAutonomousCognitionPipeline(
            reasoningPipeline =
                DefaultRuntimeAutonomousReasoningPipeline(
                    goalDeriver =
                        DefaultRuntimeGoalDeriver(),
                    planner =
                        DefaultRuntimePlanner(),
                    reasoningAnalyzer =
                        DefaultRuntimeReasoningAnalyzer()
                ),
            intentDeriver =
                DefaultRuntimeIntentDeriver(),
            strategyDeriver =
                DefaultRuntimeStrategyDeriver()
        )

    @Test
    fun stable_intelligence_forms_coherent_non_actionable_cognition() {
        val result =
            pipeline.process(
                RuntimeIntelligenceFixture.result(
                    significance =
                        RuntimeMeaningSignificance.STABLE,
                    confidence = 0.90
                )
            )

        assertTrue(result.coherent)
        assertFalse(result.actionable)

        assertEquals(
            RuntimeStrategyState.PRESERVE,
            result.strategy.state
        )

        assertEquals(
            0.90,
            result.confidence
        )
    }

    @Test
    fun warning_intelligence_forms_actionable_diagnostic_cognition() {
        val result =
            pipeline.process(
                RuntimeIntelligenceFixture.result(
                    significance =
                        RuntimeMeaningSignificance.WARNING,
                    confidence = 0.80
                )
            )

        assertTrue(result.coherent)
        assertTrue(result.actionable)

        assertEquals(
            RuntimeStrategyState.DIAGNOSE,
            result.strategy.state
        )
    }

    @Test
    fun critical_intelligence_forms_actionable_restore_cognition() {
        val result =
            pipeline.process(
                RuntimeIntelligenceFixture.result(
                    significance =
                        RuntimeMeaningSignificance.CRITICAL,
                    confidence = 0.95
                )
            )

        assertTrue(result.coherent)
        assertTrue(result.actionable)

        assertEquals(
            RuntimeStrategyState.RESTORE,
            result.strategy.state
        )
    }

    @Test
    fun result_preserves_exact_upstream_chain() {
        val result =
            pipeline.process(
                RuntimeIntelligenceFixture.result(
                    significance =
                        RuntimeMeaningSignificance.WARNING,
                    confidence = 0.80
                )
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
    fun repeated_processing_is_deterministic_for_same_input() {
        val intelligence =
            RuntimeIntelligenceFixture.result(
                significance =
                    RuntimeMeaningSignificance.CRITICAL,
                confidence = 0.91
            )

        val first =
            pipeline.process(
                intelligence
            )

        val second =
            pipeline.process(
                intelligence
            )

        assertEquals(
            first,
            second
        )
    }
}
