package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.decision.proposal.RuntimeAutonomousDecisionProposalState
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeCompositionAutonomousDecisionProposalContractTest {

    @Test
    fun composition_owns_stable_proposal_deriver() {
        val composition =
            DefaultRuntimeComposition()

        assertSame(
            composition.autonomousDecisionProposalDeriver(),
            composition.autonomousDecisionProposalDeriver()
        )
    }

    @Test
    fun separate_compositions_own_independent_proposal_derivers() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.autonomousDecisionProposalDeriver(),
            second.autonomousDecisionProposalDeriver()
        )
    }

    @Test
    fun composition_cognition_can_form_recovery_proposal() {
        val composition =
            DefaultRuntimeComposition()

        val intelligence =
            RuntimeIntelligenceFixture.result(
                significance =
                    RuntimeMeaningSignificance.CRITICAL,
                confidence = 0.95
            )

        val cognition =
            composition
                .autonomousCognitionPipeline()
                .process(intelligence)

        val proposal =
            composition
                .autonomousDecisionProposalDeriver()
                .derive(cognition)

        assertEquals(
            RuntimeAutonomousDecisionProposalState.RECOVER,
            proposal.state
        )

        assertSame(
            cognition,
            proposal.cognition
        )
    }

    @Test
    fun prepare_runtime_preserves_stateless_proposal_deriver_owner() {
        val composition =
            DefaultRuntimeComposition()

        val deriver =
            composition
                .autonomousDecisionProposalDeriver()

        composition.prepareRuntime()

        assertSame(
            deriver,
            composition
                .autonomousDecisionProposalDeriver()
        )
    }
}
