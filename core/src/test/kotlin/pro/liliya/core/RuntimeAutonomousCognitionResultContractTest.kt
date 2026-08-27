package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import pro.liliya.core.runtime.intelligence.cognition.DefaultRuntimeAutonomousCognitionPipeline
import pro.liliya.core.runtime.intelligence.goal.DefaultRuntimeGoalDeriver
import pro.liliya.core.runtime.intelligence.intent.DefaultRuntimeIntentDeriver
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.planning.DefaultRuntimePlanner
import pro.liliya.core.runtime.intelligence.reasoning.DefaultRuntimeReasoningAnalyzer
import pro.liliya.core.runtime.intelligence.reasoning.orchestration.DefaultRuntimeAutonomousReasoningPipeline
import pro.liliya.core.runtime.intelligence.strategy.DefaultRuntimeStrategyDeriver

class RuntimeAutonomousCognitionResultContractTest {

    @Test
    fun result_is_self_consistent_snapshot_of_reasoning_intent_and_strategy() {
        val pipeline =
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

        val result =
            pipeline.process(
                RuntimeIntelligenceFixture.result(
                    significance =
                        RuntimeMeaningSignificance.CRITICAL,
                    confidence = 0.87
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

        assertEquals(
            result.reasoningResult.coherent &&
                result.intent.coherent &&
                result.strategy.coherent,
            result.coherent
        )

        assertEquals(
            result.reasoningResult.actionable &&
                result.intent.actionable &&
                result.strategy.actionable,
            result.actionable
        )

        assertEquals(
            minOf(
                result.reasoningResult.confidence,
                result.intent.confidence,
                result.strategy.confidence
            ),
            result.confidence
        )
    }
}
