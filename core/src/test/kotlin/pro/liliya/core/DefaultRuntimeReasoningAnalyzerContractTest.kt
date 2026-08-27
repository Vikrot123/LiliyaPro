package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.goal.RuntimeGoal
import pro.liliya.core.runtime.intelligence.goal.RuntimeGoalPriority
import pro.liliya.core.runtime.intelligence.goal.RuntimeGoalState
import pro.liliya.core.runtime.intelligence.planning.DefaultRuntimePlanner
import pro.liliya.core.runtime.intelligence.planning.RuntimePlan
import pro.liliya.core.runtime.intelligence.planning.RuntimePlanState
import pro.liliya.core.runtime.intelligence.planning.RuntimePlanStep
import pro.liliya.core.runtime.intelligence.planning.RuntimePlanStepType
import pro.liliya.core.runtime.intelligence.reasoning.DefaultRuntimeReasoningAnalyzer
import pro.liliya.core.runtime.intelligence.reasoning.RuntimeReasoningIssue
import pro.liliya.core.runtime.intelligence.reasoning.RuntimeReasoningState

class DefaultRuntimeReasoningAnalyzerContractTest {

    private val planner =
        DefaultRuntimePlanner()

    private val analyzer =
        DefaultRuntimeReasoningAnalyzer()

    @Test
    fun planner_generated_recovery_plan_is_coherent() {
        val goal =
            goal(
                state =
                    RuntimeGoalState.RECOVER,
                priority =
                    RuntimeGoalPriority.CRITICAL,
                confidence = 0.95
            )

        val plan =
            planner.plan(goal)

        val assessment =
            analyzer.analyze(
                goal = goal,
                plan = plan
            )

        assertEquals(
            RuntimeReasoningState.COHERENT,
            assessment.state
        )

        assertTrue(assessment.coherent)
        assertTrue(assessment.actionable)
        assertTrue(assessment.issues.isEmpty())

        assertEquals(
            0.95,
            assessment.confidence
        )
    }

    @Test
    fun empty_plan_is_incomplete() {
        val goal =
            goal(
                state =
                    RuntimeGoalState.INVESTIGATE
            )

        val plan =
            RuntimePlan(
                goal = goal,
                state =
                    RuntimePlanState.INVESTIGATE,
                steps = emptyList(),
                actionable = false,
                confidence = 0.80
            )

        val assessment =
            analyzer.analyze(
                goal,
                plan
            )

        assertEquals(
            RuntimeReasoningState.INCOMPLETE,
            assessment.state
        )

        assertTrue(
            RuntimeReasoningIssue.EMPTY_PLAN
                in assessment.issues
        )

        assertTrue(
            RuntimeReasoningIssue.REQUIRED_STEP_MISSING
                in assessment.issues
        )

        assertFalse(assessment.actionable)
    }

    @Test
    fun missing_recovery_verification_is_incomplete() {
        val goal =
            goal(
                state =
                    RuntimeGoalState.RECOVER
            )

        val plan =
            RuntimePlan(
                goal = goal,
                state =
                    RuntimePlanState.RECOVER,
                steps =
                    listOf(
                        RuntimePlanStep(
                            order = 1,
                            type =
                                RuntimePlanStepType
                                    .ASSESS_HEALTH,
                            objective =
                                "Assess recovery conditions",
                            actionable = true
                        ),
                        RuntimePlanStep(
                            order = 2,
                            type =
                                RuntimePlanStepType
                                    .PREPARE_RECOVERY,
                            objective =
                                "Prepare recovery",
                            actionable = true
                        )
                    ),
                actionable = true,
                confidence = 0.80
            )

        val assessment =
            analyzer.analyze(
                goal,
                plan
            )

        assertEquals(
            RuntimeReasoningState.INCOMPLETE,
            assessment.state
        )

        assertTrue(
            RuntimeReasoningIssue.REQUIRED_STEP_MISSING
                in assessment.issues
        )
    }

    @Test
    fun invalid_step_order_is_incomplete() {
        val goal =
            goal(
                state =
                    RuntimeGoalState.OBSERVE
            )

        val plan =
            RuntimePlan(
                goal = goal,
                state =
                    RuntimePlanState.OBSERVE,
                steps =
                    listOf(
                        RuntimePlanStep(
                            order = 2,
                            type =
                                RuntimePlanStepType
                                    .OBSERVE_STATE,
                            objective =
                                "Observe state",
                            actionable = true
                        ),
                        RuntimePlanStep(
                            order = 1,
                            type =
                                RuntimePlanStepType
                                    .ASSESS_HEALTH,
                            objective =
                                "Assess health",
                            actionable = true
                        )
                    ),
                actionable = true,
                confidence = 0.80
            )

        val assessment =
            analyzer.analyze(
                goal,
                plan
            )

        assertEquals(
            RuntimeReasoningState.INCOMPLETE,
            assessment.state
        )

        assertTrue(
            RuntimeReasoningIssue.STEP_ORDER_INVALID
                in assessment.issues
        )
    }

    @Test
    fun plan_for_different_goal_is_contradictory() {
        val requestedGoal =
            goal(
                state =
                    RuntimeGoalState.RECOVER
            )

        val differentGoal =
            goal(
                state =
                    RuntimeGoalState.MAINTAIN,
                actionable = false
            )

        val plan =
            planner.plan(
                differentGoal
            )

        val assessment =
            analyzer.analyze(
                requestedGoal,
                plan
            )

        assertEquals(
            RuntimeReasoningState.CONTRADICTORY,
            assessment.state
        )

        assertTrue(
            RuntimeReasoningIssue.GOAL_MISMATCH
                in assessment.issues
        )

        assertTrue(
            RuntimeReasoningIssue.PLAN_STATE_MISMATCH
                in assessment.issues
        )

        assertFalse(assessment.actionable)
    }

    @Test
    fun non_actionable_goal_cannot_have_actionable_plan() {
        val goal =
            goal(
                state =
                    RuntimeGoalState.RECOVER,
                actionable = false
            )

        val normalPlan =
            planner.plan(
                goal.copy(
                    actionable = true
                )
            )

        val contradictoryPlan =
            normalPlan.copy(
                goal = goal,
                actionable = true
            )

        val assessment =
            analyzer.analyze(
                goal,
                contradictoryPlan
            )

        assertEquals(
            RuntimeReasoningState.CONTRADICTORY,
            assessment.state
        )

        assertTrue(
            RuntimeReasoningIssue
                .ACTIONABILITY_CONTRADICTION
                in assessment.issues
        )

        assertFalse(assessment.actionable)
    }

    private fun goal(
        state: RuntimeGoalState,
        priority: RuntimeGoalPriority =
            RuntimeGoalPriority.NORMAL,
        confidence: Double = 0.80,
        actionable: Boolean = true
    ) =
        RuntimeGoal(
            state = state,
            priority = priority,
            objective =
                "reasoning contract objective",
            confidence = confidence,
            actionable = actionable
        )
}
