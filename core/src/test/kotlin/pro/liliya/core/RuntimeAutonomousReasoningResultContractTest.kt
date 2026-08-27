package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import pro.liliya.core.runtime.intelligence.goal.DefaultRuntimeGoalDeriver
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.planning.DefaultRuntimePlanner
import pro.liliya.core.runtime.intelligence.reasoning.DefaultRuntimeReasoningAnalyzer
import pro.liliya.core.runtime.intelligence.reasoning.orchestration.DefaultRuntimeAutonomousReasoningPipeline

class RuntimeAutonomousReasoningResultContractTest {

    @Test
    fun result_is_self_consistent_snapshot_of_goal_plan_and_reasoning() {
        val pipeline =
            DefaultRuntimeAutonomousReasoningPipeline(
                goalDeriver =
                    DefaultRuntimeGoalDeriver(),
                planner =
                    DefaultRuntimePlanner(),
                reasoningAnalyzer =
                    DefaultRuntimeReasoningAnalyzer()
            )

        val result =
            pipeline.process(
                RuntimeIntelligenceFixture.result(
                    significance =
                        RuntimeMeaningSignificance.CRITICAL,
                    confidence = 0.95
                )
            )

        assertSame(
            result.goal,
            result.plan.goal
        )

        assertSame(
            result.goal,
            result.reasoning.goal
        )

        assertSame(
            result.plan,
            result.reasoning.plan
        )

        assertEquals(
            result.reasoning.coherent,
            result.coherent
        )

        assertEquals(
            result.reasoning.actionable,
            result.actionable
        )

        assertEquals(
            result.reasoning.confidence,
            result.confidence
        )
    }

    @Test
    fun separate_pipeline_invocations_produce_independent_results() {
        val pipeline =
            DefaultRuntimeAutonomousReasoningPipeline(
                goalDeriver =
                    DefaultRuntimeGoalDeriver(),
                planner =
                    DefaultRuntimePlanner(),
                reasoningAnalyzer =
                    DefaultRuntimeReasoningAnalyzer()
            )

        val first =
            pipeline.process(
                RuntimeIntelligenceFixture.result(
                    significance =
                        RuntimeMeaningSignificance.STABLE,
                    confidence = 0.90
                )
            )

        val second =
            pipeline.process(
                RuntimeIntelligenceFixture.result(
                    significance =
                        RuntimeMeaningSignificance.CRITICAL,
                    confidence = 0.95
                )
            )

        assertEquals(
            RuntimeMeaningSignificance.STABLE,
            RuntimeMeaningSignificance.STABLE
        )

        assertEquals(
            0.90,
            first.confidence
        )

        assertEquals(
            0.95,
            second.confidence
        )
    }
}
