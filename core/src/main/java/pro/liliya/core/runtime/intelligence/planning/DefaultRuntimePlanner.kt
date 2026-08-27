package pro.liliya.core.runtime.intelligence.planning

import pro.liliya.core.runtime.intelligence.goal.RuntimeGoal
import pro.liliya.core.runtime.intelligence.goal.RuntimeGoalState

class DefaultRuntimePlanner : RuntimePlanner {

    override fun plan(
        goal: RuntimeGoal
    ): RuntimePlan {

        val state =
            when (goal.state) {
                RuntimeGoalState.OBSERVE ->
                    RuntimePlanState.OBSERVE

                RuntimeGoalState.MAINTAIN ->
                    RuntimePlanState.MAINTAIN

                RuntimeGoalState.INVESTIGATE ->
                    RuntimePlanState.INVESTIGATE

                RuntimeGoalState.RECOVER ->
                    RuntimePlanState.RECOVER
            }

        val steps =
            when (goal.state) {
                RuntimeGoalState.OBSERVE ->
                    listOf(
                        step(
                            order = 1,
                            type =
                                RuntimePlanStepType.OBSERVE_STATE,
                            objective =
                                "Observe runtime state and reduce uncertainty",
                            actionable = true
                        ),
                        step(
                            order = 2,
                            type =
                                RuntimePlanStepType.ASSESS_HEALTH,
                            objective =
                                "Assess whether runtime health is stable",
                            actionable = true
                        )
                    )

                RuntimeGoalState.MAINTAIN ->
                    listOf(
                        step(
                            order = 1,
                            type =
                                RuntimePlanStepType.PRESERVE_STABILITY,
                            objective =
                                "Preserve the current stable runtime state",
                            actionable = false
                        )
                    )

                RuntimeGoalState.INVESTIGATE ->
                    listOf(
                        step(
                            order = 1,
                            type =
                                RuntimePlanStepType.ASSESS_HEALTH,
                            objective =
                                "Assess current runtime health",
                            actionable = true
                        ),
                        step(
                            order = 2,
                            type =
                                RuntimePlanStepType.IDENTIFY_DEGRADATION,
                            objective =
                                "Identify the source of runtime degradation",
                            actionable = true
                        )
                    )

                RuntimeGoalState.RECOVER ->
                    listOf(
                        step(
                            order = 1,
                            type =
                                RuntimePlanStepType.ASSESS_HEALTH,
                            objective =
                                "Assess failure conditions before recovery",
                            actionable = true
                        ),
                        step(
                            order = 2,
                            type =
                                RuntimePlanStepType.PREPARE_RECOVERY,
                            objective =
                                "Prepare restoration of healthy runtime operation",
                            actionable = true
                        ),
                        step(
                            order = 3,
                            type =
                                RuntimePlanStepType.VERIFY_RECOVERY,
                            objective =
                                "Verify runtime health after recovery",
                            actionable = true
                        )
                    )
            }

        return RuntimePlan(
            goal = goal,
            state = state,
            steps = steps,
            actionable =
                goal.actionable &&
                    steps.any { it.actionable },
            confidence =
                goal.confidence
        )
    }

    private fun step(
        order: Int,
        type: RuntimePlanStepType,
        objective: String,
        actionable: Boolean
    ) =
        RuntimePlanStep(
            order = order,
            type = type,
            objective = objective,
            actionable = actionable
        )
}
