package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning

import pro.liliya.core.runtime.intelligence.experience.decision.RuntimeExperienceDecision

data class RuntimeAutonomousExecutionExperienceCommitDecision(
    val state:
        RuntimeAutonomousExecutionExperienceCommitDecisionState,
    val shouldCommit: Boolean,
    val representation:
        RuntimeAutonomousExecutionExperienceRepresentation,
    val experienceDecision:
        RuntimeExperienceDecision,
    val reason: String
)
