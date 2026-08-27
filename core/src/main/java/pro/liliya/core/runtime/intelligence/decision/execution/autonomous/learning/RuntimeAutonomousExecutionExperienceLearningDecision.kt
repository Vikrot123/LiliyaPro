package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning

data class RuntimeAutonomousExecutionExperienceLearningDecision(
    val state: RuntimeAutonomousExecutionExperienceLearningDecisionState,
    val shouldProcessExperience: Boolean,
    val reason: String
)
