package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning

import pro.liliya.core.runtime.intelligence.experience.decision.RuntimeExperienceDecisionEngine

class DefaultRuntimeAutonomousExecutionExperienceCommitDecisionEngine(
    private val experienceDecisionEngine:
        RuntimeExperienceDecisionEngine
) : RuntimeAutonomousExecutionExperienceCommitDecisionEngine {

    override fun decide(
        learningDecision:
            RuntimeAutonomousExecutionExperienceLearningDecision,
        representation:
            RuntimeAutonomousExecutionExperienceRepresentation
    ): RuntimeAutonomousExecutionExperienceCommitDecision {

        val experienceDecision =
            experienceDecisionEngine.decide(
                representation.experience
            )

        if (
            learningDecision.state ==
                RuntimeAutonomousExecutionExperienceLearningDecisionState.REJECTED
        ) {
            return RuntimeAutonomousExecutionExperienceCommitDecision(
                state =
                    RuntimeAutonomousExecutionExperienceCommitDecisionState.REJECTED,
                shouldCommit =
                    false,
                representation =
                    representation,
                experienceDecision =
                    experienceDecision,
                reason =
                    "Autonomous experience learning decision was rejected"
            )
        }

        if (
            learningDecision.state !=
                RuntimeAutonomousExecutionExperienceLearningDecisionState.PROCESS_NOVEL_EXPERIENCE ||
            !learningDecision.shouldProcessExperience
        ) {
            return RuntimeAutonomousExecutionExperienceCommitDecision(
                state =
                    RuntimeAutonomousExecutionExperienceCommitDecisionState.SKIP,
                shouldCommit =
                    false,
                representation =
                    representation,
                experienceDecision =
                    experienceDecision,
                reason =
                    "Autonomous experience is not eligible for post-execution commit"
            )
        }

        if (!experienceDecision.shouldRemember) {
            return RuntimeAutonomousExecutionExperienceCommitDecision(
                state =
                    RuntimeAutonomousExecutionExperienceCommitDecisionState.SKIP,
                shouldCommit =
                    false,
                representation =
                    representation,
                experienceDecision =
                    experienceDecision,
                reason =
                    "Existing experience decision rejected post-execution preservation"
            )
        }

        return RuntimeAutonomousExecutionExperienceCommitDecision(
            state =
                RuntimeAutonomousExecutionExperienceCommitDecisionState.COMMIT,
            shouldCommit =
                true,
            representation =
                representation,
            experienceDecision =
                experienceDecision,
            reason =
                "Novel post-execution experience is approved for commit"
        )
    }
}
