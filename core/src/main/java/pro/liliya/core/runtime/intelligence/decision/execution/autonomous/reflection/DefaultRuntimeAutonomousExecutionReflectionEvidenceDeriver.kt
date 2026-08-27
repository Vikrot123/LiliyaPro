package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection

import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.evaluation.RuntimeAutonomousExecutionEvaluationResult
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.assessment.RuntimeAutonomousExecutionAssessmentState
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.feedback.RuntimeAutonomousExecutionFeedbackState
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.feedback.RuntimeAutonomousExecutionFeedback

class DefaultRuntimeAutonomousExecutionReflectionEvidenceDeriver :
    RuntimeAutonomousExecutionReflectionEvidenceDeriver {

    override fun derive(
        evaluation: RuntimeAutonomousExecutionEvaluationResult,
        feedback: RuntimeAutonomousExecutionFeedback
    ): RuntimeAutonomousExecutionReflectionEvidence {

        val execution =
            evaluation.executionResult.execution

        val decision =
            execution.decision

        val assessment =
            evaluation.assessment

        val expectedFeedbackState =
            when (assessment.state) {
                RuntimeAutonomousExecutionAssessmentState.NOT_REQUIRED ->
                    RuntimeAutonomousExecutionFeedbackState.NO_FEEDBACK_REQUIRED

                RuntimeAutonomousExecutionAssessmentState.EFFECTIVE ->
                    RuntimeAutonomousExecutionFeedbackState.POSITIVE

                RuntimeAutonomousExecutionAssessmentState.INEFFECTIVE ->
                    RuntimeAutonomousExecutionFeedbackState.NEGATIVE
            }

        val consistent =
            feedback.state ==
                expectedFeedbackState &&
                feedback.assessmentState ==
                    assessment.state &&
                feedback.outcomeState ==
                    assessment.outcomeState &&
                feedback.proposalState ==
                    assessment.proposalState &&
                feedback.command ==
                    assessment.command &&
                feedback.actionAttempted ==
                    assessment.actionAttempted &&
                feedback.actionSucceeded ==
                    assessment.actionSucceeded &&
                feedback.previousRuntimeState ==
                    assessment.previousRuntimeState &&
                feedback.currentRuntimeState ==
                    assessment.currentRuntimeState &&
                feedback.message ==
                    assessment.message

        return RuntimeAutonomousExecutionReflectionEvidence(
            evaluation = evaluation,
            feedback = feedback,
            decision = decision,
            command = decision.command,
            decisionReason = decision.reason,
            confidence = decision.confidence,
            actionAttempted = feedback.actionAttempted,
            actionSucceeded = feedback.actionSucceeded,
            feedbackState = feedback.state,
            knowledgeUsed =
                decision.knowledgeSelection != null,
            consistent = consistent
        )
    }
}
