package pro.liliya.core.runtime.intelligence.planning

data class RuntimePlanStep(
    val order: Int,
    val type: RuntimePlanStepType,
    val objective: String,
    val actionable: Boolean
)
