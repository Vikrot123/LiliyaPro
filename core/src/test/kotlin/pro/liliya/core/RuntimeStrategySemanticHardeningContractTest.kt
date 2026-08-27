package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import pro.liliya.core.runtime.intelligence.goal.RuntimeGoal
import pro.liliya.core.runtime.intelligence.goal.RuntimeGoalPriority
import pro.liliya.core.runtime.intelligence.goal.RuntimeGoalState
import pro.liliya.core.runtime.intelligence.intent.RuntimeIntent
import pro.liliya.core.runtime.intelligence.intent.RuntimeIntentState
import pro.liliya.core.runtime.intelligence.planning.RuntimePlan
import pro.liliya.core.runtime.intelligence.planning.RuntimePlanState
import pro.liliya.core.runtime.intelligence.planning.RuntimePlanStep
import pro.liliya.core.runtime.intelligence.planning.RuntimePlanStepType
import pro.liliya.core.runtime.intelligence.reasoning.RuntimeReasoningAssessment
import pro.liliya.core.runtime.intelligence.reasoning.RuntimeReasoningState
import pro.liliya.core.runtime.intelligence.reasoning.orchestration.RuntimeAutonomousReasoningResult
import pro.liliya.core.runtime.intelligence.strategy.DefaultRuntimeStrategyDeriver
import pro.liliya.core.runtime.intelligence.strategy.RuntimeStrategyState

class RuntimeStrategySemanticHardeningContractTest {

    private val deriver =
        DefaultRuntimeStrategyDeriver()

    @Test
    fun active_intent_with_incoherent_reasoning_result_is_deferred() {
        val intent =
            intent(
                state = RuntimeIntentState.RESTORE,
                coherent = true,
                actionable = true,
                reasoningCoherent = false,
                reasoningActionable = false
            )

        val strategy =
            deriver.derive(intent)

        assertEquals(
            RuntimeStrategyState.DEFER,
            strategy.state
        )
        assertFalse(strategy.coherent)
        assertFalse(strategy.actionable)
        assertSame(intent, strategy.intent)
    }

    @Test
    fun active_intent_cannot_restore_actionability_rejected_by_reasoning() {
        val intent =
            intent(
                state = RuntimeIntentState.INVESTIGATE,
                coherent = true,
                actionable = true,
                reasoningCoherent = true,
                reasoningActionable = false
            )

        val strategy =
            deriver.derive(intent)

        assertEquals(
            RuntimeStrategyState.DEFER,
            strategy.state
        )
        assertFalse(strategy.actionable)
        assertSame(intent, strategy.intent)
    }

    @Test
    fun repeated_derivation_is_deterministic() {
        val intent =
            intent(
                state = RuntimeIntentState.RESTORE,
                coherent = true,
                actionable = true,
                reasoningCoherent = true,
                reasoningActionable = true,
                confidence = 0.73
            )

        val first =
            deriver.derive(intent)

        val second =
            deriver.derive(intent)

        assertEquals(first, second)
        assertEquals(
            RuntimeStrategyState.RESTORE,
            first.state
        )
        assertEquals(0.73, first.confidence)
    }

    @Test
    fun deferred_strategy_preserves_input_confidence() {
        val intent =
            intent(
                state = RuntimeIntentState.RESTORE,
                coherent = true,
                actionable = true,
                reasoningCoherent = false,
                reasoningActionable = false,
                confidence = 0.41
            )

        val strategy =
            deriver.derive(intent)

        assertEquals(
            RuntimeStrategyState.DEFER,
            strategy.state
        )
        assertEquals(
            0.41,
            strategy.confidence
        )
    }

    private fun intent(
        state: RuntimeIntentState,
        coherent: Boolean,
        actionable: Boolean,
        reasoningCoherent: Boolean,
        reasoningActionable: Boolean,
        confidence: Double = 0.80
    ): RuntimeIntent {
        val goalState =
            when (state) {
                RuntimeIntentState.OBSERVE ->
                    RuntimeGoalState.OBSERVE

                RuntimeIntentState.PRESERVE ->
                    RuntimeGoalState.MAINTAIN

                RuntimeIntentState.INVESTIGATE ->
                    RuntimeGoalState.INVESTIGATE

                RuntimeIntentState.RESTORE ->
                    RuntimeGoalState.RECOVER

                RuntimeIntentState.WITHHOLD ->
                    RuntimeGoalState.OBSERVE
            }

        val goal =
            RuntimeGoal(
                state = goalState,
                priority = RuntimeGoalPriority.NORMAL,
                objective =
                    "strategy hardening contract goal",
                confidence = confidence,
                actionable = reasoningActionable
            )

        val step =
            when (goalState) {
                RuntimeGoalState.OBSERVE ->
                    RuntimePlanStep(
                        order = 1,
                        type =
                            RuntimePlanStepType.OBSERVE_STATE,
                        objective = "Observe runtime state",
                        actionable = reasoningActionable
                    )

                RuntimeGoalState.MAINTAIN ->
                    RuntimePlanStep(
                        order = 1,
                        type =
                            RuntimePlanStepType.PRESERVE_STABILITY,
                        objective = "Preserve runtime stability",
                        actionable = reasoningActionable
                    )

                RuntimeGoalState.INVESTIGATE ->
                    RuntimePlanStep(
                        order = 1,
                        type =
                            RuntimePlanStepType.ASSESS_HEALTH,
                        objective = "Assess runtime health",
                        actionable = reasoningActionable
                    )

                RuntimeGoalState.RECOVER ->
                    RuntimePlanStep(
                        order = 1,
                        type =
                            RuntimePlanStepType.PREPARE_RECOVERY,
                        objective = "Prepare runtime recovery",
                        actionable = reasoningActionable
                    )
            }

        val planState =
            when (goalState) {
                RuntimeGoalState.OBSERVE ->
                    RuntimePlanState.OBSERVE

                RuntimeGoalState.MAINTAIN ->
                    RuntimePlanState.MAINTAIN

                RuntimeGoalState.INVESTIGATE ->
                    RuntimePlanState.INVESTIGATE

                RuntimeGoalState.RECOVER ->
                    RuntimePlanState.RECOVER
            }

        val plan =
            RuntimePlan(
                goal = goal,
                state = planState,
                steps = listOf(step),
                actionable = reasoningActionable,
                confidence = confidence
            )

        val reasoning =
            RuntimeReasoningAssessment(
                goal = goal,
                plan = plan,
                state =
                    if (reasoningCoherent) {
                        RuntimeReasoningState.COHERENT
                    } else {
                        RuntimeReasoningState.CONTRADICTORY
                    },
                issues = emptyList(),
                coherent = reasoningCoherent,
                actionable = reasoningActionable,
                confidence = confidence,
                reason =
                    "strategy hardening contract reasoning"
            )

        val reasoningResult =
            RuntimeAutonomousReasoningResult(
                goal = goal,
                plan = plan,
                reasoning = reasoning,
                coherent = reasoningCoherent,
                actionable = reasoningActionable,
                confidence = confidence
            )

        return RuntimeIntent(
            state = state,
            objective =
                "strategy hardening contract intent",
            reasoningResult = reasoningResult,
            coherent = coherent,
            actionable = actionable,
            confidence = confidence,
            reason =
                "strategy hardening contract intent"
        )
    }
}
