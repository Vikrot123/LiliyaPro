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
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.assessment.DefaultRuntimeAutonomousExecutionAssessmentDeriver
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.assessment.RuntimeAutonomousExecutionAssessmentState
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.outcome.DefaultRuntimeAutonomousExecutionOutcomeDeriver
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.outcome.RuntimeAutonomousExecutionOutcomeState
import pro.liliya.core.runtime.intelligence.decision.proposal.RuntimeAutonomousDecisionProposalState
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeAutonomousExecutionAssessmentContractTest {

    @Test
    fun no_action_outcome_is_assessed_as_not_required() {
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
                    source = "assessment-stable-test",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "assessment-stable-test",
                            level = RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val outcome =
            DefaultRuntimeAutonomousExecutionOutcomeDeriver()
                .derive(pipelineResult)

        val assessment =
            DefaultRuntimeAutonomousExecutionAssessmentDeriver()
                .derive(outcome)

        assertEquals(
            RuntimeAutonomousExecutionAssessmentState.NOT_REQUIRED,
            assessment.state
        )

        assertEquals(
            RuntimeAutonomousExecutionOutcomeState.NO_ACTION,
            assessment.outcomeState
        )

        assertEquals(
            RuntimeAutonomousDecisionProposalState.NO_ACTION,
            assessment.proposalState
        )

        assertFalse(
            assessment.actionAttempted
        )

        assertNull(
            assessment.actionSucceeded
        )

        assertNull(
            assessment.command
        )

        assertNull(
            assessment.previousRuntimeState
        )

        assertNull(
            assessment.currentRuntimeState
        )

        assertNull(
            assessment.message
        )
    }

    @Test
    fun successful_health_check_outcome_is_assessed_as_effective() {
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
                    source = "assessment-success-test",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "assessment-success-test",
                            level = RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val outcome =
            DefaultRuntimeAutonomousExecutionOutcomeDeriver()
                .derive(pipelineResult)

        assertEquals(
            RuntimeAutonomousExecutionOutcomeState.SUCCEEDED,
            outcome.state,
            "assessment success fixture must use an action that actually succeeds"
        )

        val assessment =
            DefaultRuntimeAutonomousExecutionAssessmentDeriver()
                .derive(outcome)

        assertEquals(
            RuntimeAutonomousExecutionAssessmentState.EFFECTIVE,
            assessment.state
        )

        assertEquals(
            RuntimeAutonomousExecutionOutcomeState.SUCCEEDED,
            assessment.outcomeState
        )

        assertEquals(
            RuntimeAutonomousDecisionProposalState.INVESTIGATE,
            assessment.proposalState
        )

        assertEquals(
            RuntimeCommand.HEALTH_CHECK,
            assessment.command
        )

        assertTrue(
            assessment.actionAttempted
        )

        assertEquals(
            true,
            assessment.actionSucceeded
        )

        assertEquals(
            outcome.previousRuntimeState,
            assessment.previousRuntimeState
        )

        assertEquals(
            outcome.currentRuntimeState,
            assessment.currentRuntimeState
        )

        assertEquals(
            outcome.message,
            assessment.message
        )
    }

    @Test
    fun failed_recovery_outcome_is_assessed_as_ineffective() {
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
                    source = "assessment-failed-test",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "assessment-failed-test",
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

        val assessment =
            DefaultRuntimeAutonomousExecutionAssessmentDeriver()
                .derive(outcome)

        assertEquals(
            RuntimeAutonomousExecutionAssessmentState.INEFFECTIVE,
            assessment.state
        )

        assertEquals(
            RuntimeAutonomousExecutionOutcomeState.FAILED,
            assessment.outcomeState
        )

        assertEquals(
            RuntimeAutonomousDecisionProposalState.RECOVER,
            assessment.proposalState
        )

        assertEquals(
            RuntimeCommand.RECOVER,
            assessment.command
        )

        assertTrue(
            assessment.actionAttempted
        )

        assertEquals(
            false,
            assessment.actionSucceeded
        )

        assertEquals(
            outcome.previousRuntimeState,
            assessment.previousRuntimeState
        )

        assertEquals(
            outcome.currentRuntimeState,
            assessment.currentRuntimeState
        )

        assertEquals(
            outcome.message,
            assessment.message
        )
    }
}
