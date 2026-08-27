package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue
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

class DefaultRuntimeStrategyDeriverContractTest {

    private val deriver =
        DefaultRuntimeStrategyDeriver()

    @Test
    fun observe_intent_derives_monitor_strategy() {
        val intent =
            intent(
                state = RuntimeIntentState.OBSERVE,
                actionable = true
            )

        val strategy =
            deriver.derive(intent)

        assertEquals(
            RuntimeStrategyState.MONITOR,
            strategy.state
        )

        assertTrue(strategy.coherent)
        assertTrue(strategy.actionable)

        assertSame(
            intent,
            strategy.intent
        )
    }

    @Test
    fun preserve_intent_derives_preservation_strategy() {
        val strategy =
            deriver.derive(
                intent(
                    state = RuntimeIntentState.PRESERVE,
                    actionable = false
                )
            )

        assertEquals(
            RuntimeStrategyState.PRESERVE,
            strategy.state
        )

        assertFalse(strategy.actionable)
    }

    @Test
    fun investigate_intent_derives_diagnostic_strategy() {
        val strategy =
            deriver.derive(
                intent(
                    state = RuntimeIntentState.INVESTIGATE,
                    actionable = true
                )
            )

        assertEquals(
            RuntimeStrategyState.DIAGNOSE,
            strategy.state
        )

        assertTrue(strategy.actionable)
    }

    @Test
    fun restore_intent_derives_restoration_strategy() {
        val strategy =
            deriver.derive(
                intent(
                    state = RuntimeIntentState.RESTORE,
                    actionable = true
                )
            )

        assertEquals(
            RuntimeStrategyState.RESTORE,
            strategy.state
        )

        assertTrue(strategy.actionable)
    }

    @Test
    fun withheld_intent_derives_non_actionable_deferred_strategy() {
        val strategy =
            deriver.derive(
                intent(
                    state = RuntimeIntentState.WITHHOLD,
                    actionable = false,
                    coherent = false
                )
            )

        assertEquals(
            RuntimeStrategyState.DEFER,
            strategy.state
        )

        assertFalse(strategy.coherent)
        assertFalse(strategy.actionable)
    }

    @Test
    fun incoherent_intent_cannot_form_active_strategy_even_if_state_is_active() {
        val strategy =
            deriver.derive(
                intent(
                    state = RuntimeIntentState.RESTORE,
                    actionable = true,
                    coherent = false
                )
            )

        assertEquals(
            RuntimeStrategyState.DEFER,
            strategy.state
        )

        assertFalse(strategy.actionable)
    }

    @Test
    fun strategy_preserves_intent_confidence() {
        val strategy =
            deriver.derive(
                intent(
                    state = RuntimeIntentState.INVESTIGATE,
                    actionable = true,
                    confidence = 0.71
                )
            )

        assertEquals(
            0.71,
            strategy.confidence
        )
    }

    private fun intent(
        state: RuntimeIntentState,
        actionable: Boolean,
        coherent: Boolean = true,
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

                RuntimeIntentState.RESTORE,
                RuntimeIntentState.WITHHOLD ->
                    RuntimeGoalState.RECOVER
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

        val goal =
            RuntimeGoal(
                state = goalState,
                priority = RuntimeGoalPriority.NORMAL,
                objective = "strategy contract goal",
                confidence = confidence,
                actionable = actionable
            )

        val plan =
            RuntimePlan(
                goal = goal,
                state = planState,
                steps =
                    listOf(
                        RuntimePlanStep(
                            order = 1,
                            type =
                                if (actionable) {
                                    RuntimePlanStepType.ASSESS_HEALTH
                                } else {
                                    RuntimePlanStepType.PRESERVE_STABILITY
                                },
                            objective =
                                "strategy contract step",
                            actionable =
                                actionable
                        )
                    ),
                actionable = actionable,
                confidence = confidence
            )

        val reasoning =
            RuntimeReasoningAssessment(
                goal = goal,
                plan = plan,
                state =
                    if (coherent) {
                        RuntimeReasoningState.COHERENT
                    } else {
                        RuntimeReasoningState.CONTRADICTORY
                    },
                issues = emptyList(),
                coherent = coherent,
                actionable =
                    coherent && actionable,
                confidence = confidence,
                reason =
                    "strategy contract reasoning"
            )

        val reasoningResult =
            RuntimeAutonomousReasoningResult(
                goal = goal,
                plan = plan,
                reasoning = reasoning,
                coherent = reasoning.coherent,
                actionable = reasoning.actionable,
                confidence = reasoning.confidence
            )

        return RuntimeIntent(
            state = state,
            objective =
                "strategy contract intent",
            reasoningResult =
                reasoningResult,
            coherent =
                coherent,
            actionable =
                actionable,
            confidence =
                confidence,
            reason =
                "strategy contract intent"
        )
    }
}
