package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.decision.proposal.RuntimeAutonomousDecisionProposalState
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeCompositionAutonomousDecisionSynthesisChainContractTest {

    @Test
    fun critical_intelligence_flows_through_cognition_proposal_and_synthesis() {
        val composition =
            DefaultRuntimeComposition()

        val cognition =
            composition
                .autonomousCognitionPipeline()
                .process(
                    RuntimeIntelligenceFixture.result(
                        significance =
                            RuntimeMeaningSignificance.CRITICAL,
                        confidence = 0.95
                    )
                )

        val proposal =
            composition
                .autonomousDecisionProposalDeriver()
                .derive(
                    cognition
                )

        val decision =
            composition
                .autonomousDecisionSynthesizer()
                .synthesize(
                    proposal
                )

        assertSame(
            cognition,
            proposal.cognition
        )

        assertEquals(
            RuntimeAutonomousDecisionProposalState.RECOVER,
            proposal.state
        )

        assertTrue(
            proposal.coherent
        )

        assertTrue(
            proposal.actionable
        )

        assertEquals(
            RuntimeCommand.RECOVER,
            decision.command
        )

        assertEquals(
            proposal.confidence,
            decision.confidence
        )

        assertNull(
            decision.knowledgeSelection
        )
    }

    @Test
    fun warning_intelligence_flows_to_health_check_decision() {
        val composition =
            DefaultRuntimeComposition()

        val cognition =
            composition
                .autonomousCognitionPipeline()
                .process(
                    RuntimeIntelligenceFixture.result(
                        significance =
                            RuntimeMeaningSignificance.WARNING,
                        confidence = 0.80
                    )
                )

        val proposal =
            composition
                .autonomousDecisionProposalDeriver()
                .derive(
                    cognition
                )

        val decision =
            composition
                .autonomousDecisionSynthesizer()
                .synthesize(
                    proposal
                )

        assertSame(
            cognition,
            proposal.cognition
        )

        assertEquals(
            RuntimeAutonomousDecisionProposalState.INVESTIGATE,
            proposal.state
        )

        assertEquals(
            RuntimeCommand.HEALTH_CHECK,
            decision.command
        )
    }

    @Test
    fun stable_intelligence_remains_non_executing() {
        val composition =
            DefaultRuntimeComposition()

        val cognition =
            composition
                .autonomousCognitionPipeline()
                .process(
                    RuntimeIntelligenceFixture.result(
                        significance =
                            RuntimeMeaningSignificance.STABLE,
                        confidence = 0.90
                    )
                )

        val proposal =
            composition
                .autonomousDecisionProposalDeriver()
                .derive(
                    cognition
                )

        val decision =
            composition
                .autonomousDecisionSynthesizer()
                .synthesize(
                    proposal
                )

        assertSame(
            cognition,
            proposal.cognition
        )

        assertNull(
            decision.command
        )

        assertEquals(
            proposal.confidence,
            decision.confidence
        )
    }

    @Test
    fun autonomous_synthesis_does_not_dispatch_or_execute_action() {
        val composition =
            DefaultRuntimeComposition()

        val cognition =
            composition
                .autonomousCognitionPipeline()
                .process(
                    RuntimeIntelligenceFixture.result(
                        significance =
                            RuntimeMeaningSignificance.CRITICAL,
                        confidence = 0.95
                    )
                )

        val proposal =
            composition
                .autonomousDecisionProposalDeriver()
                .derive(
                    cognition
                )

        val decision =
            composition
                .autonomousDecisionSynthesizer()
                .synthesize(
                    proposal
                )

        assertEquals(
            RuntimeCommand.RECOVER,
            decision.command
        )

        /*
         * Contract boundary:
         *
         * synthesis produces RuntimeDecision only.
         * No RuntimeActionRequest is created here and
         * no dispatcher/policy/executor is invoked.
         */
    }

    @Test
    fun prepare_runtime_preserves_autonomous_decision_chain_owners() {
        val composition =
            DefaultRuntimeComposition()

        val cognitionPipeline =
            composition
                .autonomousCognitionPipeline()

        val proposalDeriver =
            composition
                .autonomousDecisionProposalDeriver()

        val synthesizer =
            composition
                .autonomousDecisionSynthesizer()

        composition.prepareRuntime()

        assertSame(
            cognitionPipeline,
            composition.autonomousCognitionPipeline()
        )

        assertSame(
            proposalDeriver,
            composition.autonomousDecisionProposalDeriver()
        )

        assertSame(
            synthesizer,
            composition.autonomousDecisionSynthesizer()
        )
    }
}
