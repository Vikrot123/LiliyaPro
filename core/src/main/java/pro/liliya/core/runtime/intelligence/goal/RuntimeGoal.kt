package pro.liliya.core.runtime.intelligence.goal

data class RuntimeGoal(
    val state: RuntimeGoalState,
    val priority: RuntimeGoalPriority,
    val objective: String,
    val confidence: Double,
    val actionable: Boolean
)
