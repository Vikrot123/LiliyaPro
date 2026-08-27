package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertSame
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.decision.proposal.RuntimeAutonomousDecisionProposal
import pro.liliya.core.runtime.intelligence.decision.proposal.RuntimeAutonomousDecisionProposalState
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeCompositionAutonomousDecisionSynthesizerContractTest {

    @Test
    fun composition_owns_stable_autonomous_decision_synthesizer() {
        val composition =
            DefaultRuntimeComposition()

        assertSame(
            composition.autonomousDecisionSynthesizer(),
            composition.autonomousDecisionSynthesizer()
        )
    }

    @Test
    fun separate_compositions_own_independent_synthesizers() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.autonomousDecisionSynthesizer(),
            second.autonomousDecisionSynthesizer()
        )
    }

    @Test
    fun composition_synthesizer_maps_recovery_proposal_to_recover_command() {
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
            RuntimeAutonomousDecisionProposal(
                state =
                    RuntimeAutonomousDecisionProposalState.RECOVER,
                cognition = cognition,
                coherent = cognition.coherent,
                actionable = cognition.actionable,
                confidence = cognition.confidence,
                objective =
                    "Restore healthy runtime operation",
                reason =
                    "Autonomous recovery proposal"
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

        assertEquals(
            proposal.confidence,
            decision.confidence
        )

        assertNull(
            decision.knowledgeSelection
        )
    }

    @Test
    fun composition_synthesizer_fails_closed_for_incoherent_proposal() {
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
            RuntimeAutonomousDecisionProposal(
                state =
                    RuntimeAutonomousDecisionProposalState.RECOVER,
                cognition = cognition,
                coherent = false,
                actionable = true,
                confidence = cognition.confidence,
                objective =
                    "Invalid incoherent recovery proposal",
                reason =
                    "Composition synthesizer fail-closed contract"
            )

        val decision =
            composition
                .autonomousDecisionSynthesizer()
                .synthesize(
                    proposal
                )

        assertNull(
            decision.command
        )
    }

    @Test
    fun prepare_runtime_preserves_stateless_synthesizer_owner() {
        val composition =
            DefaultRuntimeComposition()

        val synthesizer =
            composition
                .autonomousDecisionSynthesizer()

        composition.prepareRuntime()

        assertSame(
            synthesizer,
            composition.autonomousDecisionSynthesizer()
        )
    }
}
