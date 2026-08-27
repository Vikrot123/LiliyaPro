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
import pro.liliya.core.runtime.intelligence.planning.RuntimePlanStepType

class RuntimeGoalPlanningFoundationContractTest {

    private val goalDeriver =
        DefaultRuntimeGoalDeriver()

    private val planner =
        DefaultRuntimePlanner()

    @Test
    fun stable_intelligence_flows_from_goal_into_maintenance_plan() {
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

        assertEquals(
            RuntimeGoalState.MAINTAIN,
            goal.state
        )

        assertEquals(
            RuntimePlanState.MAINTAIN,
            plan.state
        )

        assertFalse(plan.actionable)
    }

    @Test
    fun warning_intelligence_flows_into_investigation_plan() {
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

        assertEquals(
            RuntimeGoalState.INVESTIGATE,
            goal.state
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
    fun critical_intelligence_flows_into_recovery_plan() {
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

        assertEquals(
            RuntimeGoalState.RECOVER,
            goal.state
        )

        assertEquals(
            RuntimePlanState.RECOVER,
            plan.state
        )

        assertEquals(
            RuntimePlanStepType.PREPARE_RECOVERY,
            plan.steps[1].type
        )

        assertTrue(plan.actionable)
    }
}
