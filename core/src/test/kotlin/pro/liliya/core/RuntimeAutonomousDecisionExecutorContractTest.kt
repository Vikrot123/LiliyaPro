package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.action.RuntimeActionResult
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.decision.proposal.RuntimeAutonomousDecisionProposal
import pro.liliya.core.runtime.intelligence.decision.proposal.RuntimeAutonomousDecisionProposalState
import pro.liliya.core.runtime.intelligence.decision.synthesis.DefaultRuntimeAutonomousDecisionSynthesizer
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.DefaultRuntimeAutonomousDecisionExecutor
import pro.liliya.core.runtime.intelligence.decision.DefaultRuntimeDecisionActionRequestFactory
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.policy.RuntimeActionPolicyDecision
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeAutonomousDecisionExecutorContractTest {

    @Test
    fun no_action_proposal_never_enters_action_pipeline() {
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
            RuntimeAutonomousDecisionProposal(
                state =
                    RuntimeAutonomousDecisionProposalState.NO_ACTION,
                cognition = cognition,
                coherent = true,
                actionable = false,
                confidence = cognition.confidence,
                objective =
                    "Preserve healthy runtime",
                reason =
                    "No autonomous action required"
            )

        val result =
            composition
                .autonomousDecisionExecutor()
                .execute(
                    proposal = proposal,
                    source = "autonomous-cognition",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "autonomous-cognition",
                            level = RuntimeAuthorityLevel.SYSTEM
                        )
                )

        assertSame(
            proposal,
            result.proposal
        )

        assertNull(
            result.decision.command
        )

        assertNull(
            result.request
        )

        assertNull(
            result.actionResult
        )
    }

    @Test
    fun system_recovery_proposal_enters_existing_governed_action_pipeline() {
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

        val result =
            composition
                .autonomousDecisionExecutor()
                .execute(
                    proposal = proposal,
                    source = "autonomous-cognition",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "autonomous-cognition",
                            level = RuntimeAuthorityLevel.SYSTEM
                        )
                )

        assertSame(
            proposal,
            result.proposal
        )

        assertEquals(
            RuntimeCommand.RECOVER,
            result.decision.command
        )

        assertNotNull(
            result.request
        )

        assertEquals(
            RuntimeCommand.RECOVER,
            result.request?.command
        )

        assertEquals(
            RuntimeAuthorityLevel.SYSTEM,
            result.request
                ?.authority
                ?.level
        )

        val actionResult =
            assertNotNull(
                result.actionResult
            )

        assertSame(
            result.request,
            actionResult.request,
            "dispatcher result must preserve the exact governed request"
        )

        val audit =
            composition
                .actionAuditProvider()
                .snapshot()
                .last()

        assertSame(
            result.request,
            audit.request,
            "policy audit must preserve the exact autonomous request"
        )

        assertEquals(
            RuntimeActionPolicyDecision.ALLOW,
            audit.policyDecision,
            "SYSTEM autonomous recovery must pass existing policy"
        )

        assertEquals(
            RuntimeAuthorityLevel.SYSTEM,
            audit.authorityLevel,
            "SYSTEM authority must remain unchanged through governance"
        )

        assertEquals(
            RuntimeAuthorityLevel.SYSTEM,
            audit.actualAuthority,
            "policy must evaluate the supplied SYSTEM authority"
        )
    }

    @Test
    fun user_recovery_proposal_cannot_bypass_existing_policy() {
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

        val result =
            composition
                .autonomousDecisionExecutor()
                .execute(
                    proposal = proposal,
                    source = "autonomous-user-context",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "autonomous-user-context",
                            level = RuntimeAuthorityLevel.USER
                        )
                )

        assertEquals(
            RuntimeCommand.RECOVER,
            result.decision.command
        )

        assertNotNull(
            result.request
        )

        assertEquals(
            RuntimeAuthorityLevel.USER,
            result.request
                ?.authority
                ?.level
        )

        val actionResult =
            assertNotNull(
                result.actionResult
            )

        assertEquals(
            false,
            actionResult.success,
            "USER autonomous recovery must be denied by existing policy"
        )

        assertEquals(
            false,
            actionResult.controlResult.success,
            "denied autonomous recovery must not reach successful runtime control"
        )

        assertSame(
            result.request,
            actionResult.request,
            "dispatcher result must preserve the exact governed request"
        )

        val audit =
            composition
                .actionAuditProvider()
                .snapshot()
                .last()

        assertSame(
            result.request,
            audit.request,
            "policy audit must preserve the exact autonomous request"
        )

        assertEquals(
            RuntimeActionPolicyDecision.DENY,
            audit.policyDecision,
            "USER autonomous recovery must remain denied by existing policy"
        )

        assertEquals(
            RuntimeAuthorityLevel.USER,
            audit.authorityLevel,
            "USER authority must remain unchanged through governance"
        )

        assertEquals(
            RuntimeAuthorityLevel.USER,
            audit.actualAuthority,
            "policy must evaluate the supplied USER authority"
        )
    }

    @Test
    fun executor_never_escalates_supplied_authority() {
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

        val authority =
            RuntimeActionAuthorityContext(
                source = "autonomous-user-context",
                level = RuntimeAuthorityLevel.USER
            )

        val result =
            composition
                .autonomousDecisionExecutor()
                .execute(
                    proposal = proposal,
                    source = authority.source,
                    authority = authority
                )

        assertSame(
            authority,
            result.request?.authority
        )
    }

    @Test
    fun incoherent_proposal_fails_closed_before_dispatch() {
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
                    "Invalid autonomous recovery",
                reason =
                    "Incoherent proposal fixture"
            )

        val result =
            composition
                .autonomousDecisionExecutor()
                .execute(
                    proposal = proposal,
                    source = "autonomous-cognition",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "autonomous-cognition",
                            level = RuntimeAuthorityLevel.SYSTEM
                        )
                )

        assertNull(
            result.decision.command
        )

        assertNull(
            result.request
        )

        assertNull(
            result.actionResult
        )
    }

    @Test
    fun executor_preserves_exact_synthesized_decision_and_request_chain() {
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

        val result =
            composition
                .autonomousDecisionExecutor()
                .execute(
                    proposal = proposal,
                    source = "autonomous-cognition",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "autonomous-cognition",
                            level = RuntimeAuthorityLevel.SYSTEM
                        )
                )

        assertSame(
            proposal,
            result.proposal
        )

        assertEquals(
            proposal.confidence,
            result.decision.confidence
        )

        assertEquals(
            RuntimeCommand.HEALTH_CHECK,
            result.request?.command
        )
    }
}
