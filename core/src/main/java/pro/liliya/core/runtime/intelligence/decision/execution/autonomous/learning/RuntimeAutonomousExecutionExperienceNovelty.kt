package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning

data class RuntimeAutonomousExecutionExperienceNovelty(
    val state: RuntimeAutonomousExecutionExperienceNoveltyState,
    val novel: Boolean,
    val actionAttempted: Boolean,
    val actionSucceeded: Boolean?,
    val reason: String
)
