package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.decision.proposal.RuntimeAutonomousDecisionProposalState
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeAutonomousExecutionPipelineContractTest {

    @Test
    fun critical_intelligence_flows_through_complete_governed_autonomous_chain() {
        val composition =
            DefaultRuntimeComposition()

        val intelligence =
            RuntimeIntelligenceFixture.result(
                significance =
                    RuntimeMeaningSignificance.CRITICAL,
                confidence = 0.95
            )

        val authority =
            RuntimeActionAuthorityContext(
                source = "autonomous-pipeline-test",
                level = RuntimeAuthorityLevel.SYSTEM
            )

        val result =
            composition
                .autonomousExecutionPipeline()
                .process(
                    intelligence = intelligence,
                    source = "autonomous-pipeline-test",
                    authority = authority
                )

        assertSame(
            intelligence,
            result.intelligence
        )

        assertSame(
            result.cognition,
            result.proposal.cognition
        )

        assertSame(
            result.proposal,
            result.execution.proposal
        )

        assertEquals(
            RuntimeAutonomousDecisionProposalState.RECOVER,
            result.proposal.state
        )

        assertEquals(
            RuntimeCommand.RECOVER,
            result.execution.decision.command
        )

        val request =
            assertNotNull(
                result.execution.request
            )

        assertSame(
            authority,
            request.authority
        )

        val actionResult =
            assertNotNull(
                result.execution.actionResult
            )

        assertSame(
            request,
            actionResult.request
        )
    }

    @Test
    fun stable_intelligence_completes_chain_without_action_dispatch() {
        val composition =
            DefaultRuntimeComposition()

        val intelligence =
            RuntimeIntelligenceFixture.result(
                significance =
                    RuntimeMeaningSignificance.STABLE,
                confidence = 0.90
            )

        val result =
            composition
                .autonomousExecutionPipeline()
                .process(
                    intelligence = intelligence,
                    source = "autonomous-stable-test",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "autonomous-stable-test",
                            level = RuntimeAuthorityLevel.SYSTEM
                        )
                )

        assertTrue(
            result.cognition.coherent
        )

        assertFalse(
            result.cognition.actionable
        )

        assertEquals(
            RuntimeAutonomousDecisionProposalState.NO_ACTION,
            result.proposal.state
        )

        assertNull(
            result.execution.decision.command
        )

        assertNull(
            result.execution.request
        )

        assertNull(
            result.execution.actionResult
        )
    }

    @Test
    fun warning_intelligence_flows_to_governed_health_check() {
        val composition =
            DefaultRuntimeComposition()

        val authority =
            RuntimeActionAuthorityContext(
                source = "autonomous-warning-test",
                level = RuntimeAuthorityLevel.SYSTEM
            )

        val result =
            composition
                .autonomousExecutionPipeline()
                .process(
                    intelligence =
                        RuntimeIntelligenceFixture.result(
                            significance =
                                RuntimeMeaningSignificance.WARNING,
                            confidence = 0.80
                        ),
                    source = "autonomous-warning-test",
                    authority = authority
                )

        assertEquals(
            RuntimeAutonomousDecisionProposalState.INVESTIGATE,
            result.proposal.state
        )

        assertEquals(
            RuntimeCommand.HEALTH_CHECK,
            result.execution.decision.command
        )

        assertSame(
            authority,
            assertNotNull(
                result.execution.request
            ).authority
        )

        assertNotNull(
            result.execution.actionResult
        )
    }
}
