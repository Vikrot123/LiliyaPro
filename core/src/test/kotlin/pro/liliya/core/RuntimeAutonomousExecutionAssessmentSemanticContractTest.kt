package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.assessment.DefaultRuntimeAutonomousExecutionAssessmentDeriver
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.assessment.RuntimeAutonomousExecutionAssessmentState
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.outcome.RuntimeAutonomousExecutionOutcome
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.outcome.RuntimeAutonomousExecutionOutcomeState
import pro.liliya.core.runtime.intelligence.decision.proposal.RuntimeAutonomousDecisionProposalState

class RuntimeAutonomousExecutionAssessmentSemanticContractTest {

    private val deriver =
        DefaultRuntimeAutonomousExecutionAssessmentDeriver()

    @Test
    fun no_action_maps_to_not_required() {
        val assessment =
            deriver.derive(
                RuntimeAutonomousExecutionOutcome(
                    state =
                        RuntimeAutonomousExecutionOutcomeState.NO_ACTION,
                    proposalState =
                        RuntimeAutonomousDecisionProposalState.NO_ACTION,
                    command = null,
                    actionAttempted = false,
                    actionSucceeded = null,
                    previousRuntimeState = null,
                    currentRuntimeState = null,
                    message = null
                )
            )

        assertEquals(
            RuntimeAutonomousExecutionAssessmentState.NOT_REQUIRED,
            assessment.state
        )
    }

    @Test
    fun succeeded_maps_to_effective() {
        val assessment =
            deriver.derive(
                RuntimeAutonomousExecutionOutcome(
                    state =
                        RuntimeAutonomousExecutionOutcomeState.SUCCEEDED,
                    proposalState =
                        RuntimeAutonomousDecisionProposalState.INVESTIGATE,
                    command =
                        RuntimeCommand.HEALTH_CHECK,
                    actionAttempted = true,
                    actionSucceeded = true,
                    previousRuntimeState = null,
                    currentRuntimeState = null,
                    message = "success"
                )
            )

        assertEquals(
            RuntimeAutonomousExecutionAssessmentState.EFFECTIVE,
            assessment.state
        )
    }

    @Test
    fun failed_maps_to_ineffective() {
        val assessment =
            deriver.derive(
                RuntimeAutonomousExecutionOutcome(
                    state =
                        RuntimeAutonomousExecutionOutcomeState.FAILED,
                    proposalState =
                        RuntimeAutonomousDecisionProposalState.RECOVER,
                    command =
                        RuntimeCommand.RECOVER,
                    actionAttempted = true,
                    actionSucceeded = false,
                    previousRuntimeState = null,
                    currentRuntimeState = null,
                    message = "failed"
                )
            )

        assertEquals(
            RuntimeAutonomousExecutionAssessmentState.INEFFECTIVE,
            assessment.state
        )
    }
}
