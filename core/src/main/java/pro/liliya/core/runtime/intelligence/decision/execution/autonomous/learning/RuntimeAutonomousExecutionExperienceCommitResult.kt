package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning

import pro.liliya.core.runtime.intelligence.experience.RuntimeExperience

data class RuntimeAutonomousExecutionExperienceCommitResult(
    val state: RuntimeAutonomousExecutionExperienceCommitState,
    val committed: Boolean,
    val experience: RuntimeExperience,
    val decision: RuntimeAutonomousExecutionExperienceCommitDecision,
    val reason: String
)
