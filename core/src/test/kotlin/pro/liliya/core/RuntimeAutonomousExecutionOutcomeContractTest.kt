package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.outcome.DefaultRuntimeAutonomousExecutionOutcomeDeriver
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.outcome.RuntimeAutonomousExecutionOutcomeState
import pro.liliya.core.runtime.intelligence.decision.proposal.RuntimeAutonomousDecisionProposalState
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeAutonomousExecutionOutcomeContractTest {

    @Test
    fun stable_pipeline_result_derives_no_action_outcome() {
        val composition =
            DefaultRuntimeComposition()

        val pipelineResult =
            composition
                .autonomousExecutionPipeline()
                .process(
                    intelligence =
                        RuntimeIntelligenceFixture.result(
                            significance =
                                RuntimeMeaningSignificance.STABLE,
                            confidence = 0.90
                        ),
                    source = "outcome-stable-test",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "outcome-stable-test",
                            level = RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val outcome =
            DefaultRuntimeAutonomousExecutionOutcomeDeriver()
                .derive(pipelineResult)

        assertEquals(
            RuntimeAutonomousExecutionOutcomeState.NO_ACTION,
            outcome.state
        )

        assertEquals(
            RuntimeAutonomousDecisionProposalState.NO_ACTION,
            outcome.proposalState
        )

        assertFalse(
            outcome.actionAttempted
        )

        assertNull(
            outcome.actionSucceeded
        )

        assertNull(
            outcome.command
        )

        assertNull(
            outcome.previousRuntimeState
        )

        assertNull(
            outcome.currentRuntimeState
        )

        assertNull(
            outcome.message
        )
    }

    @Test
    fun successful_autonomous_health_check_derives_success_outcome() {
        val composition =
            DefaultRuntimeComposition()

        val pipelineResult =
            composition
                .autonomousExecutionPipeline()
                .process(
                    intelligence =
                        RuntimeIntelligenceFixture.result(
                            significance =
                                RuntimeMeaningSignificance.WARNING,
                            confidence = 0.80
                        ),
                    source = "outcome-success-test",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "outcome-success-test",
                            level = RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val actionResult =
            pipelineResult.execution.actionResult
                ?: error("governed HEALTH_CHECK action result expected")

        assertEquals(
            true,
            actionResult.success,
            "success fixture must use an action that actually succeeds"
        )

        val outcome =
            DefaultRuntimeAutonomousExecutionOutcomeDeriver()
                .derive(pipelineResult)

        assertEquals(
            RuntimeAutonomousExecutionOutcomeState.SUCCEEDED,
            outcome.state
        )

        assertEquals(
            RuntimeAutonomousDecisionProposalState.INVESTIGATE,
            outcome.proposalState
        )

        assertEquals(
            RuntimeCommand.HEALTH_CHECK,
            outcome.command
        )

        assertTrue(
            outcome.actionAttempted
        )

        assertEquals(
            true,
            outcome.actionSucceeded
        )

        assertEquals(
            actionResult.controlResult.previousState,
            outcome.previousRuntimeState
        )

        assertEquals(
            actionResult.controlResult.currentState,
            outcome.currentRuntimeState
        )

        assertEquals(
            actionResult.controlResult.message,
            outcome.message
        )
    }

    @Test
    fun denied_autonomous_action_derives_failed_outcome() {
        val composition =
            DefaultRuntimeComposition()

        val pipelineResult =
            composition
                .autonomousExecutionPipeline()
                .process(
                    intelligence =
                        RuntimeIntelligenceFixture.result(
                            significance =
                                RuntimeMeaningSignificance.CRITICAL,
                            confidence = 0.95
                        ),
                    source = "outcome-denied-test",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "outcome-denied-test",
                            level = RuntimeAuthorityLevel.USER
                        )
                )

        val outcome =
            DefaultRuntimeAutonomousExecutionOutcomeDeriver()
                .derive(pipelineResult)

        assertEquals(
            RuntimeAutonomousExecutionOutcomeState.FAILED,
            outcome.state
        )

        assertEquals(
            RuntimeAutonomousDecisionProposalState.RECOVER,
            outcome.proposalState
        )

        assertEquals(
            RuntimeCommand.RECOVER,
            outcome.command
        )

        assertTrue(
            outcome.actionAttempted
        )

        assertEquals(
            false,
            outcome.actionSucceeded
        )
    }
}
