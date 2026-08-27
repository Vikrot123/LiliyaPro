package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.feedback

import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.assessment.RuntimeAutonomousExecutionAssessment
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.assessment.RuntimeAutonomousExecutionAssessmentState

class DefaultRuntimeAutonomousExecutionFeedbackDeriver :
    RuntimeAutonomousExecutionFeedbackDeriver {

    override fun derive(
        assessment: RuntimeAutonomousExecutionAssessment
    ): RuntimeAutonomousExecutionFeedback {

        val state =
            when (assessment.state) {
                RuntimeAutonomousExecutionAssessmentState.NOT_REQUIRED ->
                    RuntimeAutonomousExecutionFeedbackState.NO_FEEDBACK_REQUIRED

                RuntimeAutonomousExecutionAssessmentState.EFFECTIVE ->
                    RuntimeAutonomousExecutionFeedbackState.POSITIVE

                RuntimeAutonomousExecutionAssessmentState.INEFFECTIVE ->
                    RuntimeAutonomousExecutionFeedbackState.NEGATIVE
            }

        return RuntimeAutonomousExecutionFeedback(
            state = state,
            assessmentState = assessment.state,
            outcomeState = assessment.outcomeState,
            proposalState = assessment.proposalState,
            command = assessment.command,
            actionAttempted = assessment.actionAttempted,
            actionSucceeded = assessment.actionSucceeded,
            previousRuntimeState = assessment.previousRuntimeState,
            currentRuntimeState = assessment.currentRuntimeState,
            message = assessment.message
        )
    }
}
