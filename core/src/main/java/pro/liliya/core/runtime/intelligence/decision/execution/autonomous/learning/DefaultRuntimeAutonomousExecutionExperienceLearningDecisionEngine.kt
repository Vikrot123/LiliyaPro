package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning

import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection.RuntimeAutonomousExecutionReflectionAnalysisResult

class DefaultRuntimeAutonomousExecutionExperienceLearningDecisionEngine :
    RuntimeAutonomousExecutionExperienceLearningDecisionEngine {

    override fun decide(
        analysis: RuntimeAutonomousExecutionReflectionAnalysisResult
    ): RuntimeAutonomousExecutionExperienceLearningDecision {

        if (!analysis.evidence.consistent) {
            return rejected()
        }

        return alreadyRepresented()
    }

    override fun decide(
        analysis: RuntimeAutonomousExecutionReflectionAnalysisResult,
        novelty: RuntimeAutonomousExecutionExperienceNovelty
    ): RuntimeAutonomousExecutionExperienceLearningDecision {

        if (!analysis.evidence.consistent) {
            return rejected()
        }

        val feedback =
            analysis.evidence.feedback

        val noveltyConsistent =
            novelty.actionAttempted ==
                feedback.actionAttempted &&
                novelty.actionSucceeded ==
                    feedback.actionSucceeded

        if (!noveltyConsistent) {
            return RuntimeAutonomousExecutionExperienceLearningDecision(
                state =
                    RuntimeAutonomousExecutionExperienceLearningDecisionState.REJECTED,
                shouldProcessExperience =
                    false,
                reason =
                    "Autonomous execution novelty is inconsistent with reflection evidence"
            )
        }

        if (!novelty.novel) {
            return alreadyRepresented()
        }

        return RuntimeAutonomousExecutionExperienceLearningDecision(
            state =
                RuntimeAutonomousExecutionExperienceLearningDecisionState.PROCESS_NOVEL_EXPERIENCE,
            shouldProcessExperience =
                true,
            reason =
                "Autonomous execution produced novel post-execution experience"
        )
    }

    private fun rejected():
        RuntimeAutonomousExecutionExperienceLearningDecision {

        return RuntimeAutonomousExecutionExperienceLearningDecision(
            state =
                RuntimeAutonomousExecutionExperienceLearningDecisionState.REJECTED,
            shouldProcessExperience =
                false,
            reason =
                "Autonomous reflection evidence is inconsistent"
        )
    }

    private fun alreadyRepresented():
        RuntimeAutonomousExecutionExperienceLearningDecision {

        return RuntimeAutonomousExecutionExperienceLearningDecision(
            state =
                RuntimeAutonomousExecutionExperienceLearningDecisionState.ALREADY_REPRESENTED,
            shouldProcessExperience =
                false,
            reason =
                "The originating intelligence cycle already processed this experience context"
        )
    }
}
