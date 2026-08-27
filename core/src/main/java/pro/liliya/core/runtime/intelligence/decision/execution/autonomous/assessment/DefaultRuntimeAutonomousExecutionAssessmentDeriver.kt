package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.assessment

import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.outcome.RuntimeAutonomousExecutionOutcome
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.outcome.RuntimeAutonomousExecutionOutcomeState

class DefaultRuntimeAutonomousExecutionAssessmentDeriver :
    RuntimeAutonomousExecutionAssessmentDeriver {

    override fun derive(
        outcome: RuntimeAutonomousExecutionOutcome
    ): RuntimeAutonomousExecutionAssessment {

        val assessmentState =
            when (outcome.state) {
                RuntimeAutonomousExecutionOutcomeState.NO_ACTION ->
                    RuntimeAutonomousExecutionAssessmentState.NOT_REQUIRED

                RuntimeAutonomousExecutionOutcomeState.SUCCEEDED ->
                    RuntimeAutonomousExecutionAssessmentState.EFFECTIVE

                RuntimeAutonomousExecutionOutcomeState.FAILED ->
                    RuntimeAutonomousExecutionAssessmentState.INEFFECTIVE
            }

        return RuntimeAutonomousExecutionAssessment(
            state = assessmentState,
            outcomeState = outcome.state,
            proposalState = outcome.proposalState,
            command = outcome.command,
            actionAttempted = outcome.actionAttempted,
            actionSucceeded = outcome.actionSucceeded,
            previousRuntimeState = outcome.previousRuntimeState,
            currentRuntimeState = outcome.currentRuntimeState,
            message = outcome.message
        )
    }
}
