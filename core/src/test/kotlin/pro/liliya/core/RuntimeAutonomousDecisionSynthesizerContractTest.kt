package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.cognition.RuntimeAutonomousCognitionResult
import pro.liliya.core.runtime.intelligence.decision.proposal.RuntimeAutonomousDecisionProposal
import pro.liliya.core.runtime.intelligence.decision.proposal.RuntimeAutonomousDecisionProposalState
import pro.liliya.core.runtime.intelligence.decision.synthesis.DefaultRuntimeAutonomousDecisionSynthesizer
import pro.liliya.core.runtime.intelligence.goal.DefaultRuntimeGoalDeriver
import pro.liliya.core.runtime.intelligence.intent.DefaultRuntimeIntentDeriver
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.planning.DefaultRuntimePlanner
import pro.liliya.core.runtime.intelligence.reasoning.DefaultRuntimeReasoningAnalyzer
import pro.liliya.core.runtime.intelligence.reasoning.orchestration.DefaultRuntimeAutonomousReasoningPipeline
import pro.liliya.core.runtime.intelligence.strategy.DefaultRuntimeStrategyDeriver

class RuntimeAutonomousDecisionSynthesizerContractTest {

    private val synthesizer =
        DefaultRuntimeAutonomousDecisionSynthesizer()

    @Test
    fun no_action_proposal_produces_no_command() {
        val proposal = proposal(
            state = RuntimeAutonomousDecisionProposalState.NO_ACTION,
            coherent = true,
            actionable = false
        )

        val decision = synthesizer.synthesize(proposal)

        assertNull(decision.command)
        assertEquals(proposal.confidence, decision.confidence)
    }

    @Test
    fun observe_proposal_produces_no_command() {
        val proposal = proposal(
            state = RuntimeAutonomousDecisionProposalState.OBSERVE,
            coherent = true,
            actionable = true
        )

        val decision = synthesizer.synthesize(proposal)

        assertNull(decision.command)
    }

    @Test
    fun investigate_proposal_produces_health_check() {
        val proposal = proposal(
            state = RuntimeAutonomousDecisionProposalState.INVESTIGATE,
            coherent = true,
            actionable = true
        )

        val decision = synthesizer.synthesize(proposal)

        assertEquals(
            RuntimeCommand.HEALTH_CHECK,
            decision.command
        )
    }

    @Test
    fun recover_proposal_produces_recovery_command() {
        val proposal = proposal(
            state = RuntimeAutonomousDecisionProposalState.RECOVER,
            coherent = true,
            actionable = true
        )

        val decision = synthesizer.synthesize(proposal)

        assertEquals(
            RuntimeCommand.RECOVER,
            decision.command
        )
    }

    @Test
    fun withhold_proposal_produces_no_command() {
        val proposal = proposal(
            state = RuntimeAutonomousDecisionProposalState.WITHHOLD,
            coherent = false,
            actionable = false
        )

        val decision = synthesizer.synthesize(proposal)

        assertNull(decision.command)
    }

    @Test
    fun incoherent_recovery_proposal_fails_closed() {
        val proposal = proposal(
            state = RuntimeAutonomousDecisionProposalState.RECOVER,
            coherent = false,
            actionable = true
        )

        val decision = synthesizer.synthesize(proposal)

        assertNull(decision.command)
    }

    @Test
    fun non_actionable_recovery_proposal_fails_closed() {
        val proposal = proposal(
            state = RuntimeAutonomousDecisionProposalState.RECOVER,
            coherent = true,
            actionable = false
        )

        val decision = synthesizer.synthesize(proposal)

        assertNull(decision.command)
    }

    @Test
    fun decision_preserves_proposal_confidence() {
        val proposal = proposal(
            state = RuntimeAutonomousDecisionProposalState.INVESTIGATE,
            coherent = true,
            actionable = true,
            confidence = 0.73
        )

        val decision = synthesizer.synthesize(proposal)

        assertEquals(0.73, decision.confidence)
    }

    @Test
    fun synthesis_does_not_attach_legacy_knowledge_selection() {
        val proposal = proposal(
            state = RuntimeAutonomousDecisionProposalState.RECOVER,
            coherent = true,
            actionable = true
        )

        val decision = synthesizer.synthesize(proposal)

        assertNull(decision.knowledgeSelection)
    }

    @Test
    fun proposal_preserves_exact_cognition_snapshot() {
        val cognition = cognition()

        val proposal =
            RuntimeAutonomousDecisionProposal(
                state =
                    RuntimeAutonomousDecisionProposalState.RECOVER,
                cognition = cognition,
                coherent = true,
                actionable = true,
                confidence = cognition.confidence,
                objective =
                    "Restore healthy runtime operation",
                reason =
                    "Autonomous recovery proposal"
            )

        assertSame(
            cognition,
            proposal.cognition
        )
    }

    private fun proposal(
        state: RuntimeAutonomousDecisionProposalState,
        coherent: Boolean,
        actionable: Boolean,
        confidence: Double = 0.80
    ): RuntimeAutonomousDecisionProposal {
        return RuntimeAutonomousDecisionProposal(
            state = state,
            cognition = cognition(confidence),
            coherent = coherent,
            actionable = actionable,
            confidence = confidence,
            objective = "Autonomous decision synthesis contract",
            reason = "Autonomous decision synthesis contract"
        )
    }

    private fun cognition(
        confidence: Double = 0.80
    ): RuntimeAutonomousCognitionResult {
        val intelligence =
            RuntimeIntelligenceFixture.result(
                significance =
                    RuntimeMeaningSignificance.WARNING,
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
            ).process(intelligence)

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
