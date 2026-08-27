package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.cognition.RuntimeAutonomousCognitionResult
import pro.liliya.core.runtime.intelligence.decision.proposal.DefaultRuntimeAutonomousDecisionProposalDeriver
import pro.liliya.core.runtime.intelligence.decision.proposal.RuntimeAutonomousDecisionProposalState
import pro.liliya.core.runtime.intelligence.goal.DefaultRuntimeGoalDeriver
import pro.liliya.core.runtime.intelligence.intent.DefaultRuntimeIntentDeriver
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.planning.DefaultRuntimePlanner
import pro.liliya.core.runtime.intelligence.reasoning.DefaultRuntimeReasoningAnalyzer
import pro.liliya.core.runtime.intelligence.reasoning.orchestration.DefaultRuntimeAutonomousReasoningPipeline
import pro.liliya.core.runtime.intelligence.strategy.DefaultRuntimeStrategyDeriver

class DefaultRuntimeAutonomousDecisionProposalDeriverContractTest {

    private val deriver =
        DefaultRuntimeAutonomousDecisionProposalDeriver()

    @Test
    fun stable_cognition_proposes_no_action() {
        val cognition =
            cognition(
                RuntimeMeaningSignificance.STABLE,
                0.90
            )

        val proposal =
            deriver.derive(cognition)

        assertEquals(
            RuntimeAutonomousDecisionProposalState.NO_ACTION,
            proposal.state
        )

        assertFalse(proposal.actionable)
        assertTrue(proposal.coherent)
        assertSame(cognition, proposal.cognition)
    }

    @Test
    fun warning_cognition_proposes_investigation() {
        val cognition =
            cognition(
                RuntimeMeaningSignificance.WARNING,
                0.80
            )

        val proposal =
            deriver.derive(cognition)

        assertEquals(
            RuntimeAutonomousDecisionProposalState.INVESTIGATE,
            proposal.state
        )

        assertTrue(proposal.actionable)
    }

    @Test
    fun critical_cognition_proposes_recovery() {
        val cognition =
            cognition(
                RuntimeMeaningSignificance.CRITICAL,
                0.95
            )

        val proposal =
            deriver.derive(cognition)

        assertEquals(
            RuntimeAutonomousDecisionProposalState.RECOVER,
            proposal.state
        )

        assertTrue(proposal.actionable)
        assertEquals(0.95, proposal.confidence)
    }

    @Test
    fun incoherent_cognition_fails_closed() {
        val coherent =
            cognition(
                RuntimeMeaningSignificance.CRITICAL,
                0.67
            )

        val cognition =
            RuntimeAutonomousCognitionResult(
                reasoningResult =
                    coherent.reasoningResult,
                intent =
                    coherent.intent,
                strategy =
                    coherent.strategy,
                coherent = false,
                actionable = true,
                confidence = 0.67
            )

        val proposal =
            deriver.derive(cognition)

        assertEquals(
            RuntimeAutonomousDecisionProposalState.WITHHOLD,
            proposal.state
        )

        assertFalse(proposal.coherent)
        assertFalse(proposal.actionable)
        assertEquals(0.67, proposal.confidence)
    }

    @Test
    fun proposal_cannot_restore_actionability_rejected_by_cognition() {
        val active =
            cognition(
                RuntimeMeaningSignificance.WARNING,
                0.73
            )

        val cognition =
            RuntimeAutonomousCognitionResult(
                reasoningResult =
                    active.reasoningResult,
                intent =
                    active.intent,
                strategy =
                    active.strategy,
                coherent = true,
                actionable = false,
                confidence = 0.73
            )

        val proposal =
            deriver.derive(cognition)

        assertEquals(
            RuntimeAutonomousDecisionProposalState.INVESTIGATE,
            proposal.state
        )

        assertFalse(proposal.actionable)
    }

    private fun cognition(
        significance: RuntimeMeaningSignificance,
        confidence: Double
    ): RuntimeAutonomousCognitionResult {

        val intelligence =
            RuntimeIntelligenceFixture.result(
                significance = significance,
                confidence = confidence
            )

        val reasoning =
            DefaultRuntimeAutonomousReasoningPipeline(
                goalDeriver =
                    DefaultRuntimeGoalDeriver(),
                planner =
                    DefaultRuntimePlanner(),
                reasoningAnalyzer =
                    DefaultRuntimeReasoningAnalyzer()
            )
                .process(intelligence)

        val intent =
            DefaultRuntimeIntentDeriver()
                .derive(reasoning)

        val strategy =
            DefaultRuntimeStrategyDeriver()
                .derive(intent)

        return RuntimeAutonomousCognitionResult(
            reasoningResult = reasoning,
            intent = intent,
            strategy = strategy,
            coherent =
                reasoning.coherent &&
                    intent.coherent &&
                    strategy.coherent,
            actionable =
                reasoning.actionable &&
                    intent.actionable &&
                    strategy.actionable,
            confidence =
                minOf(
                    reasoning.confidence,
                    intent.confidence,
                    strategy.confidence
                )
        )
    }
}
