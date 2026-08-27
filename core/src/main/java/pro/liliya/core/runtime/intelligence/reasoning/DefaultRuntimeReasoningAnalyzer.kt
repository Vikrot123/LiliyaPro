package pro.liliya.core.runtime.intelligence.reasoning

import pro.liliya.core.runtime.intelligence.goal.RuntimeGoal
import pro.liliya.core.runtime.intelligence.goal.RuntimeGoalState
import pro.liliya.core.runtime.intelligence.planning.RuntimePlan
import pro.liliya.core.runtime.intelligence.planning.RuntimePlanState
import pro.liliya.core.runtime.intelligence.planning.RuntimePlanStepType

class DefaultRuntimeReasoningAnalyzer :
    RuntimeReasoningAnalyzer {

    override fun analyze(
        goal: RuntimeGoal,
        plan: RuntimePlan
    ): RuntimeReasoningAssessment {

        val issues =
            mutableListOf<RuntimeReasoningIssue>()

        if (plan.goal != goal) {
            issues +=
                RuntimeReasoningIssue.GOAL_MISMATCH
        }

        if (
            plan.state !=
            expectedPlanState(goal.state)
        ) {
            issues +=
                RuntimeReasoningIssue.PLAN_STATE_MISMATCH
        }

        if (plan.steps.isEmpty()) {
            issues +=
                RuntimeReasoningIssue.EMPTY_PLAN
        }

        val expectedOrders =
            (1..plan.steps.size).toList()

        val actualOrders =
            plan.steps.map {
                it.order
            }

        if (
            plan.steps.isNotEmpty() &&
            actualOrders != expectedOrders
        ) {
            issues +=
                RuntimeReasoningIssue.STEP_ORDER_INVALID
        }

        val actualTypes =
            plan.steps.map {
                it.type
            }.toSet()

        val requiredTypes =
            requiredStepTypes(goal.state)

        val missingRequiredStep =
            requiredTypes
                .any { required ->
                    required !in actualTypes
                }

        if (missingRequiredStep) {
            issues +=
                RuntimeReasoningIssue.REQUIRED_STEP_MISSING
        }

        val unexpectedStep =
            actualTypes
                .any { actual ->
                    actual !in requiredTypes
                }

        if (unexpectedStep) {
            issues +=
                RuntimeReasoningIssue.UNEXPECTED_STEP
        }

        val actionabilityContradiction =
            when {
                goal.actionable &&
                    plan.steps.isNotEmpty() &&
                    !plan.actionable ->
                    true

                !goal.actionable &&
                    plan.actionable ->
                    true

                plan.actionable &&
                    plan.steps.none {
                        it.actionable
                    } ->
                    true

                else ->
                    false
            }

        if (actionabilityContradiction) {
            issues +=
                RuntimeReasoningIssue
                    .ACTIONABILITY_CONTRADICTION
        }

        val state =
            when {
                issues.any {
                    it ==
                        RuntimeReasoningIssue.GOAL_MISMATCH ||
                        it ==
                        RuntimeReasoningIssue.PLAN_STATE_MISMATCH ||
                        it ==
                        RuntimeReasoningIssue
                            .ACTIONABILITY_CONTRADICTION
                } ->
                    RuntimeReasoningState.CONTRADICTORY

                issues.isNotEmpty() ->
                    RuntimeReasoningState.INCOMPLETE

                else ->
                    RuntimeReasoningState.COHERENT
            }

        val reason =
            when (state) {
                RuntimeReasoningState.COHERENT ->
                    "Plan is coherent with its autonomous goal"

                RuntimeReasoningState.INCOMPLETE ->
                    "Plan is incomplete for its autonomous goal"

                RuntimeReasoningState.CONTRADICTORY ->
                    "Plan contradicts its autonomous goal or internal semantics"
            }

        return RuntimeReasoningAssessment(
            goal = goal,
            plan = plan,
            state = state,
            issues = issues.toList(),
            coherent =
                state ==
                    RuntimeReasoningState.COHERENT,
            actionable =
                state ==
                    RuntimeReasoningState.COHERENT &&
                    goal.actionable &&
                    plan.actionable,
            confidence =
                minOf(
                    goal.confidence,
                    plan.confidence
                ),
            reason =
                reason
        )
    }

    private fun expectedPlanState(
        goalState: RuntimeGoalState
    ): RuntimePlanState {

        return when (goalState) {
            RuntimeGoalState.OBSERVE ->
                RuntimePlanState.OBSERVE

            RuntimeGoalState.MAINTAIN ->
                RuntimePlanState.MAINTAIN

            RuntimeGoalState.INVESTIGATE ->
                RuntimePlanState.INVESTIGATE

            RuntimeGoalState.RECOVER ->
                RuntimePlanState.RECOVER
        }
    }

    private fun requiredStepTypes(
        goalState: RuntimeGoalState
    ): Set<RuntimePlanStepType> {

        return when (goalState) {
            RuntimeGoalState.OBSERVE ->
                setOf(
                    RuntimePlanStepType.OBSERVE_STATE,
                    RuntimePlanStepType.ASSESS_HEALTH
                )

            RuntimeGoalState.MAINTAIN ->
                setOf(
                    RuntimePlanStepType.PRESERVE_STABILITY
                )

            RuntimeGoalState.INVESTIGATE ->
                setOf(
                    RuntimePlanStepType.ASSESS_HEALTH,
                    RuntimePlanStepType
                        .IDENTIFY_DEGRADATION
                )

            RuntimeGoalState.RECOVER ->
                setOf(
                    RuntimePlanStepType.ASSESS_HEALTH,
                    RuntimePlanStepType.PREPARE_RECOVERY,
                    RuntimePlanStepType.VERIFY_RECOVERY
                )
        }
    }
}
