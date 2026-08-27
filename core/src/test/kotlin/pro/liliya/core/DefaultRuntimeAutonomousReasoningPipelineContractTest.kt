package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.goal.DefaultRuntimeGoalDeriver
import pro.liliya.core.runtime.intelligence.goal.RuntimeGoalState
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.planning.DefaultRuntimePlanner
import pro.liliya.core.runtime.intelligence.planning.RuntimePlanState
import pro.liliya.core.runtime.intelligence.reasoning.DefaultRuntimeReasoningAnalyzer
import pro.liliya.core.runtime.intelligence.reasoning.RuntimeReasoningState
import pro.liliya.core.runtime.intelligence.reasoning.orchestration.DefaultRuntimeAutonomousReasoningPipeline

class DefaultRuntimeAutonomousReasoningPipelineContractTest {

    private val pipeline =
        DefaultRuntimeAutonomousReasoningPipeline(
            goalDeriver =
                DefaultRuntimeGoalDeriver(),
            planner =
                DefaultRuntimePlanner(),
            reasoningAnalyzer =
                DefaultRuntimeReasoningAnalyzer()
        )

    @Test
    fun stable_intelligence_forms_coherent_maintenance_result() {
        val result =
            pipeline.process(
                RuntimeIntelligenceFixture.result(
                    significance =
                        RuntimeMeaningSignificance.STABLE,
                    confidence = 0.90
                )
            )

        assertEquals(
            RuntimeGoalState.MAINTAIN,
            result.goal.state
        )

        assertEquals(
            RuntimePlanState.MAINTAIN,
            result.plan.state
        )

        assertEquals(
            RuntimeReasoningState.COHERENT,
            result.reasoning.state
        )

        assertTrue(
            result.coherent
        )

        assertFalse(
            result.actionable
        )

        assertEquals(
            0.90,
            result.confidence
        )
    }

    @Test
    fun warning_intelligence_forms_actionable_investigation_result() {
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

        assertEquals(
            RuntimePlanState.INVESTIGATE,
            result.plan.state
        )

        assertTrue(
            result.coherent
        )

        assertTrue(
            result.actionable
        )
    }

    @Test
    fun critical_intelligence_forms_actionable_recovery_result() {
        val result =
            pipeline.process(
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
            RuntimePlanState.RECOVER,
            result.plan.state
        )

        assertEquals(
            RuntimeReasoningState.COHERENT,
            result.reasoning.state
        )

        assertTrue(
            result.actionable
        )

        assertEquals(
            0.95,
            result.confidence
        )
    }

    @Test
    fun result_preserves_exact_goal_and_plan_used_by_reasoning() {
        val result =
            pipeline.process(
                RuntimeIntelligenceFixture.result(
                    significance =
                        RuntimeMeaningSignificance.WARNING,
                    confidence = 0.80
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
    }
}
