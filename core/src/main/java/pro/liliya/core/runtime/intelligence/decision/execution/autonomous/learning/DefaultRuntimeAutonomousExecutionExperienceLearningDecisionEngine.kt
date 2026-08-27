package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning

import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection.RuntimeAutonomousExecutionReflectionAnalysisResult

class DefaultRuntimeAutonomousExecutionExperienceLearningDecisionEngine :
    RuntimeAutonomousExecutionExperienceLearningDecisionEngine {

    override fun decide(
        analysis: RuntimeAutonomousExecutionReflectionAnalysisResult
    ): RuntimeAutonomousExecutionExperienceLearningDecision {

        if (!analysis.evidence.consistent) {
            return RuntimeAutonomousExecutionExperienceLearningDecision(
                state =
                    RuntimeAutonomousExecutionExperienceLearningDecisionState.REJECTED,
                shouldProcessExperience =
                    false,
                reason =
                    "Autonomous reflection evidence is inconsistent"
            )
        }

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
