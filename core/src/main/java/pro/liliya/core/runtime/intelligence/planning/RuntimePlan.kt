package pro.liliya.core.runtime.intelligence.planning

import pro.liliya.core.runtime.intelligence.goal.RuntimeGoal

data class RuntimePlan(
    val goal: RuntimeGoal,
    val state: RuntimePlanState,
    val steps: List<RuntimePlanStep>,
    val actionable: Boolean,
    val confidence: Double
)
