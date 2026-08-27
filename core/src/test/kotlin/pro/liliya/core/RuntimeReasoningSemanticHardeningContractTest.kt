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

class RuntimeReasoningSemanticHardeningContractTest {

    private val analyzer =
        DefaultRuntimeReasoningAnalyzer()

    private val planner =
        DefaultRuntimePlanner()

    @Test
    fun otherwise_complete_plan_with_foreign_step_is_incomplete() {
        val goal =
            goal(
                state = RuntimeGoalState.INVESTIGATE
            )

        val valid =
            planner.plan(goal)

        val plan =
            valid.copy(
                steps =
                    valid.steps +
                        RuntimePlanStep(
                            order =
                                valid.steps.size + 1,
                            type =
                                RuntimePlanStepType
                                    .PREPARE_RECOVERY,
                            objective =
                                "foreign recovery step",
                            actionable = true
                        )
            )

        val assessment =
            analyzer.analyze(
                goal = goal,
                plan = plan
            )

        assertEquals(
            RuntimeReasoningState.INCOMPLETE,
            assessment.state
        )

        assertTrue(
            RuntimeReasoningIssue.UNEXPECTED_STEP
                in assessment.issues
        )

        assertFalse(
            assessment.coherent
        )

        assertFalse(
            assessment.actionable
        )
    }

    @Test
    fun actionable_goal_with_non_actionable_plan_is_contradictory() {
        val goal =
            goal(
                state = RuntimeGoalState.INVESTIGATE,
                actionable = true
            )

        val valid =
            planner.plan(goal)

        val contradictory =
            valid.copy(
                actionable = false
            )

        val assessment =
            analyzer.analyze(
                goal = goal,
                plan = contradictory
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

        assertFalse(
            assessment.actionable
        )
    }

    @Test
    fun duplicate_step_order_is_invalid() {
        val goal =
            goal(
                RuntimeGoalState.INVESTIGATE
            )

        val valid =
            planner.plan(goal)

        val plan =
            valid.copy(
                steps =
                    listOf(
                        valid.steps[0],
                        valid.steps[1].copy(
                            order = 1
                        )
                    )
            )

        val assessment =
            analyzer.analyze(
                goal,
                plan
            )

        assertTrue(
            RuntimeReasoningIssue.STEP_ORDER_INVALID
                in assessment.issues
        )

        assertEquals(
            RuntimeReasoningState.INCOMPLETE,
            assessment.state
        )
    }

    @Test
    fun gapped_step_order_is_invalid() {
        val goal =
            goal(
                RuntimeGoalState.RECOVER
            )

        val valid =
            planner.plan(goal)

        val plan =
            valid.copy(
                steps =
                    listOf(
                        valid.steps[0],
                        valid.steps[1].copy(
                            order = 3
                        ),
                        valid.steps[2].copy(
                            order = 4
                        )
                    )
            )

        val assessment =
            analyzer.analyze(
                goal,
                plan
            )

        assertTrue(
            RuntimeReasoningIssue.STEP_ORDER_INVALID
                in assessment.issues
        )

        assertEquals(
            RuntimeReasoningState.INCOMPLETE,
            assessment.state
        )
    }

    @Test
    fun issue_order_is_deterministic_for_same_invalid_plan() {
        val goal =
            goal(
                RuntimeGoalState.RECOVER
            )

        val differentGoal =
            goal(
                RuntimeGoalState.INVESTIGATE
            )

        val invalid =
            RuntimePlan(
                goal = differentGoal,
                state =
                    RuntimePlanState.INVESTIGATE,
                steps =
                    listOf(
                        RuntimePlanStep(
                            order = 2,
                            type =
                                RuntimePlanStepType
                                    .IDENTIFY_DEGRADATION,
                            objective =
                                "invalid deterministic fixture",
                            actionable = false
                        )
                    ),
                actionable = false,
                confidence = 0.40
            )

        val first =
            analyzer.analyze(
                goal,
                invalid
            )

        val second =
            analyzer.analyze(
                goal,
                invalid
            )

        assertEquals(
            first.issues,
            second.issues
        )

        assertEquals(
            first,
            second
        )
    }

    @Test
    fun reasoning_confidence_is_minimum_of_goal_and_plan() {
        val goal =
            goal(
                state = RuntimeGoalState.RECOVER,
                confidence = 0.92
            )

        val plan =
            planner
                .plan(goal)
                .copy(
                    confidence = 0.61
                )

        val assessment =
            analyzer.analyze(
                goal,
                plan
            )

        assertEquals(
            0.61,
            assessment.confidence
        )
    }

    @Test
    fun lower_goal_confidence_bounds_reasoning_confidence() {
        val goal =
            goal(
                state = RuntimeGoalState.RECOVER,
                confidence = 0.44
            )

        val plan =
            planner
                .plan(goal)
                .copy(
                    confidence = 0.95
                )

        val assessment =
            analyzer.analyze(
                goal,
                plan
            )

        assertEquals(
            0.44,
            assessment.confidence
        )
    }

    @Test
    fun repeated_analysis_does_not_mutate_plan_or_goal() {
        val goal =
            goal(
                RuntimeGoalState.RECOVER
            )

        val plan =
            planner.plan(goal)

        val originalGoal =
            goal.copy()

        val originalPlan =
            plan.copy(
                steps =
                    plan.steps.map {
                        it.copy()
                    }
            )

        val first =
            analyzer.analyze(
                goal,
                plan
            )

        val second =
            analyzer.analyze(
                goal,
                plan
            )

        assertEquals(
            originalGoal,
            goal
        )

        assertEquals(
            originalPlan,
            plan
        )

        assertEquals(
            first,
            second
        )

        assertTrue(
            first.coherent
        )
    }

    private fun goal(
        state: RuntimeGoalState,
        confidence: Double = 0.80,
        actionable: Boolean =
            state != RuntimeGoalState.MAINTAIN
    ) =
        RuntimeGoal(
            state = state,
            priority =
                when (state) {
                    RuntimeGoalState.MAINTAIN ->
                        RuntimeGoalPriority.LOW

                    RuntimeGoalState.OBSERVE ->
                        RuntimeGoalPriority.NORMAL

                    RuntimeGoalState.INVESTIGATE ->
                        RuntimeGoalPriority.HIGH

                    RuntimeGoalState.RECOVER ->
                        RuntimeGoalPriority.CRITICAL
                },
            objective =
                "reasoning hardening objective",
            confidence = confidence,
            actionable = actionable
        )
}
