package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.goal.RuntimeGoal
import pro.liliya.core.runtime.intelligence.goal.RuntimeGoalPriority
import pro.liliya.core.runtime.intelligence.goal.RuntimeGoalState
import pro.liliya.core.runtime.intelligence.intent.DefaultRuntimeIntentDeriver
import pro.liliya.core.runtime.intelligence.intent.RuntimeIntentState
import pro.liliya.core.runtime.intelligence.planning.RuntimePlan
import pro.liliya.core.runtime.intelligence.planning.RuntimePlanState
import pro.liliya.core.runtime.intelligence.planning.RuntimePlanStep
import pro.liliya.core.runtime.intelligence.planning.RuntimePlanStepType
import pro.liliya.core.runtime.intelligence.reasoning.RuntimeReasoningAssessment
import pro.liliya.core.runtime.intelligence.reasoning.RuntimeReasoningIssue
import pro.liliya.core.runtime.intelligence.reasoning.RuntimeReasoningState
import pro.liliya.core.runtime.intelligence.reasoning.orchestration.RuntimeAutonomousReasoningResult

class DefaultRuntimeIntentDeriverContractTest {

    private val deriver =
        DefaultRuntimeIntentDeriver()

    @Test
    fun coherent_maintenance_reasoning_derives_preserve_intent() {
        val result =
            result(
                goalState = RuntimeGoalState.MAINTAIN,
                planState = RuntimePlanState.MAINTAIN,
                actionable = false
            )

        val intent =
            deriver.derive(result)

        assertEquals(
            RuntimeIntentState.PRESERVE,
            intent.state
        )

        assertTrue(intent.coherent)
        assertFalse(intent.actionable)

        assertSame(
            result,
            intent.reasoningResult
        )
    }

    @Test
    fun coherent_observation_reasoning_derives_observe_intent() {
        val intent =
            deriver.derive(
                result(
                    goalState =
                        RuntimeGoalState.OBSERVE,
                    planState =
                        RuntimePlanState.OBSERVE,
                    actionable = true
                )
            )

        assertEquals(
            RuntimeIntentState.OBSERVE,
            intent.state
        )

        assertTrue(intent.actionable)
    }

    @Test
    fun coherent_investigation_reasoning_derives_investigate_intent() {
        val intent =
            deriver.derive(
                result(
                    goalState =
                        RuntimeGoalState.INVESTIGATE,
                    planState =
                        RuntimePlanState.INVESTIGATE,
                    actionable = true
                )
            )

        assertEquals(
            RuntimeIntentState.INVESTIGATE,
            intent.state
        )

        assertTrue(intent.actionable)
    }

    @Test
    fun coherent_recovery_reasoning_derives_restore_intent() {
        val intent =
            deriver.derive(
                result(
                    goalState =
                        RuntimeGoalState.RECOVER,
                    planState =
                        RuntimePlanState.RECOVER,
                    actionable = true
                )
            )

        assertEquals(
            RuntimeIntentState.RESTORE,
            intent.state
        )

        assertTrue(intent.actionable)
    }

    @Test
    fun incomplete_reasoning_withholds_autonomous_intent() {
        val result =
            result(
                goalState =
                    RuntimeGoalState.INVESTIGATE,
                planState =
                    RuntimePlanState.INVESTIGATE,
                actionable = false,
                reasoningState =
                    RuntimeReasoningState.INCOMPLETE,
                issues =
                    listOf(
                        RuntimeReasoningIssue.REQUIRED_STEP_MISSING
                    )
            )

        val intent =
            deriver.derive(result)

        assertEquals(
            RuntimeIntentState.WITHHOLD,
            intent.state
        )

        assertFalse(intent.coherent)
        assertFalse(intent.actionable)
    }

    @Test
    fun contradictory_reasoning_withholds_autonomous_intent() {
        val result =
            result(
                goalState =
                    RuntimeGoalState.RECOVER,
                planState =
                    RuntimePlanState.RECOVER,
                actionable = false,
                reasoningState =
                    RuntimeReasoningState.CONTRADICTORY,
                issues =
                    listOf(
                        RuntimeReasoningIssue
                            .ACTIONABILITY_CONTRADICTION
                    )
            )

        val intent =
            deriver.derive(result)

        assertEquals(
            RuntimeIntentState.WITHHOLD,
            intent.state
        )

        assertFalse(intent.actionable)
    }

    @Test
    fun intent_confidence_preserves_reasoning_confidence() {
        val result =
            result(
                goalState =
                    RuntimeGoalState.INVESTIGATE,
                planState =
                    RuntimePlanState.INVESTIGATE,
                actionable = true,
                confidence = 0.73
            )

        assertEquals(
            0.73,
            deriver.derive(result).confidence
        )
    }

    private fun result(
        goalState: RuntimeGoalState,
        planState: RuntimePlanState,
        actionable: Boolean,
        confidence: Double = 0.80,
        reasoningState:
            RuntimeReasoningState =
            RuntimeReasoningState.COHERENT,
        issues:
            List<RuntimeReasoningIssue> =
            emptyList()
    ): RuntimeAutonomousReasoningResult {

        val goal =
            RuntimeGoal(
                state = goalState,
                priority =
                    RuntimeGoalPriority.NORMAL,
                objective =
                    "intent contract goal",
                confidence = confidence,
                actionable = actionable
            )

        val plan =
            RuntimePlan(
                goal = goal,
                state = planState,
                steps =
                    if (actionable) {
                        listOf(
                            RuntimePlanStep(
                                order = 1,
                                type =
                                    RuntimePlanStepType.ASSESS_HEALTH,
                                objective =
                                    "intent contract step",
                                actionable = true
                            )
                        )
                    } else {
                        listOf(
                            RuntimePlanStep(
                                order = 1,
                                type =
                                    RuntimePlanStepType.PRESERVE_STABILITY,
                                objective =
                                    "intent contract stability",
                                actionable = false
                            )
                        )
                    },
                actionable = actionable,
                confidence = confidence
            )

        val reasoning =
            RuntimeReasoningAssessment(
                goal = goal,
                plan = plan,
                state = reasoningState,
                issues = issues,
                coherent =
                    reasoningState ==
                        RuntimeReasoningState.COHERENT,
                actionable =
                    reasoningState ==
                        RuntimeReasoningState.COHERENT &&
                        actionable,
                confidence = confidence,
                reason =
                    "intent contract reasoning"
            )

        return RuntimeAutonomousReasoningResult(
            goal = goal,
            plan = plan,
            reasoning = reasoning,
            coherent =
                reasoning.coherent,
            actionable =
                reasoning.actionable,
            confidence =
                reasoning.confidence
        )
    }
}
