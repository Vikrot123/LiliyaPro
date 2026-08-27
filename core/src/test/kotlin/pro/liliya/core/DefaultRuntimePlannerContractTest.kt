package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.goal.RuntimeGoal
import pro.liliya.core.runtime.intelligence.goal.RuntimeGoalPriority
import pro.liliya.core.runtime.intelligence.goal.RuntimeGoalState
import pro.liliya.core.runtime.intelligence.planning.DefaultRuntimePlanner
import pro.liliya.core.runtime.intelligence.planning.RuntimePlanState
import pro.liliya.core.runtime.intelligence.planning.RuntimePlanStepType

class DefaultRuntimePlannerContractTest {

    private val planner =
        DefaultRuntimePlanner()

    @Test
    fun maintenance_goal_produces_non_actionable_stability_plan() {
        val goal =
            goal(
                state = RuntimeGoalState.MAINTAIN,
                actionable = false
            )

        val plan =
            planner.plan(goal)

        assertEquals(
            RuntimePlanState.MAINTAIN,
            plan.state
        )

        assertEquals(
            listOf(
                RuntimePlanStepType.PRESERVE_STABILITY
            ),
            plan.steps.map { it.type }
        )

        assertFalse(plan.actionable)
        assertEquals(goal, plan.goal)
        assertEquals(goal.confidence, plan.confidence)
    }

    @Test
    fun observation_goal_produces_ordered_observation_plan() {
        val plan =
            planner.plan(
                goal(
                    state = RuntimeGoalState.OBSERVE
                )
            )

        assertEquals(
            RuntimePlanState.OBSERVE,
            plan.state
        )

        assertEquals(
            listOf(1, 2),
            plan.steps.map { it.order }
        )

        assertEquals(
            listOf(
                RuntimePlanStepType.OBSERVE_STATE,
                RuntimePlanStepType.ASSESS_HEALTH
            ),
            plan.steps.map { it.type }
        )

        assertTrue(plan.actionable)
    }

    @Test
    fun investigation_goal_produces_health_then_degradation_steps() {
        val plan =
            planner.plan(
                goal(
                    state = RuntimeGoalState.INVESTIGATE
                )
            )

        assertEquals(
            RuntimePlanState.INVESTIGATE,
            plan.state
        )

        assertEquals(
            listOf(
                RuntimePlanStepType.ASSESS_HEALTH,
                RuntimePlanStepType.IDENTIFY_DEGRADATION
            ),
            plan.steps.map { it.type }
        )

        assertTrue(plan.actionable)
    }

    @Test
    fun recovery_goal_produces_ordered_recovery_plan() {
        val plan =
            planner.plan(
                goal(
                    state = RuntimeGoalState.RECOVER,
                    priority = RuntimeGoalPriority.CRITICAL,
                    confidence = 0.95
                )
            )

        assertEquals(
            RuntimePlanState.RECOVER,
            plan.state
        )

        assertEquals(
            listOf(1, 2, 3),
            plan.steps.map { it.order }
        )

        assertEquals(
            listOf(
                RuntimePlanStepType.ASSESS_HEALTH,
                RuntimePlanStepType.PREPARE_RECOVERY,
                RuntimePlanStepType.VERIFY_RECOVERY
            ),
            plan.steps.map { it.type }
        )

        assertTrue(plan.actionable)

        assertEquals(
            0.95,
            plan.confidence
        )
    }

    @Test
    fun planner_does_not_override_non_actionable_goal() {
        val plan =
            planner.plan(
                goal(
                    state = RuntimeGoalState.RECOVER,
                    actionable = false
                )
            )

        assertFalse(plan.actionable)

        assertTrue(
            plan.steps.any { it.actionable }
        )
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
                "planner contract objective",
            confidence = confidence,
            actionable = actionable
        )
}
