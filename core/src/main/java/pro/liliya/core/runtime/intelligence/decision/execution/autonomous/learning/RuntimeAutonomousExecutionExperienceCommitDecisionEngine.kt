package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning

interface RuntimeAutonomousExecutionExperienceCommitDecisionEngine {

    fun decide(
        learningDecision:
            RuntimeAutonomousExecutionExperienceLearningDecision,
        representation:
            RuntimeAutonomousExecutionExperienceRepresentation
    ): RuntimeAutonomousExecutionExperienceCommitDecision
}
