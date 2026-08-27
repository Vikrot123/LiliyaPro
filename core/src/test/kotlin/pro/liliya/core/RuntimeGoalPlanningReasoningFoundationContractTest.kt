package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.goal.DefaultRuntimeGoalDeriver
import pro.liliya.core.runtime.intelligence.goal.RuntimeGoalState
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.planning.DefaultRuntimePlanner
import pro.liliya.core.runtime.intelligence.planning.RuntimePlanState
import pro.liliya.core.runtime.intelligence.reasoning.DefaultRuntimeReasoningAnalyzer
import pro.liliya.core.runtime.intelligence.reasoning.RuntimeReasoningState

class RuntimeGoalPlanningReasoningFoundationContractTest {

    private val goalDeriver =
        DefaultRuntimeGoalDeriver()

    private val planner =
        DefaultRuntimePlanner()

    private val reasoning =
        DefaultRuntimeReasoningAnalyzer()

    @Test
    fun stable_intelligence_forms_coherent_non_actionable_chain() {
        val goal =
            goalDeriver.derive(
                RuntimeIntelligenceFixture.result(
                    significance =
                        RuntimeMeaningSignificance.STABLE,
                    confidence = 0.90
                )
            )

        val plan =
            planner.plan(goal)

        val assessment =
            reasoning.analyze(
                goal,
                plan
            )

        assertEquals(
            RuntimeGoalState.MAINTAIN,
            goal.state
        )

        assertEquals(
            RuntimePlanState.MAINTAIN,
            plan.state
        )

        assertEquals(
            RuntimeReasoningState.COHERENT,
            assessment.state
        )

        assertTrue(assessment.coherent)
        assertFalse(assessment.actionable)
    }

    @Test
    fun warning_intelligence_forms_coherent_investigation_chain() {
        val goal =
            goalDeriver.derive(
                RuntimeIntelligenceFixture.result(
                    significance =
                        RuntimeMeaningSignificance.WARNING,
                    confidence = 0.80
                )
            )

        val plan =
            planner.plan(goal)

        val assessment =
            reasoning.analyze(
                goal,
                plan
            )

        assertEquals(
            RuntimeGoalState.INVESTIGATE,
            goal.state
        )

        assertEquals(
            RuntimePlanState.INVESTIGATE,
            plan.state
        )

        assertEquals(
            RuntimeReasoningState.COHERENT,
            assessment.state
        )

        assertTrue(assessment.actionable)
    }

    @Test
    fun critical_intelligence_forms_coherent_recovery_chain() {
        val goal =
            goalDeriver.derive(
                RuntimeIntelligenceFixture.result(
                    significance =
                        RuntimeMeaningSignificance.CRITICAL,
                    confidence = 0.95
                )
            )

        val plan =
            planner.plan(goal)

        val assessment =
            reasoning.analyze(
                goal,
                plan
            )

        assertEquals(
            RuntimeGoalState.RECOVER,
            goal.state
        )

        assertEquals(
            RuntimePlanState.RECOVER,
            plan.state
        )

        assertEquals(
            RuntimeReasoningState.COHERENT,
            assessment.state
        )

        assertTrue(assessment.coherent)
        assertTrue(assessment.actionable)
        assertEquals(
            0.95,
            assessment.confidence
        )
    }
}
