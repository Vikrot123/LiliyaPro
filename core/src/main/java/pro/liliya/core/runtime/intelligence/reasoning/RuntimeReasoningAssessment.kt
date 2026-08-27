package pro.liliya.core.runtime.intelligence.reasoning

import pro.liliya.core.runtime.intelligence.goal.RuntimeGoal
import pro.liliya.core.runtime.intelligence.planning.RuntimePlan

data class RuntimeReasoningAssessment(
    val goal: RuntimeGoal,
    val plan: RuntimePlan,
    val state: RuntimeReasoningState,
    val issues: List<RuntimeReasoningIssue>,
    val coherent: Boolean,
    val actionable: Boolean,
    val confidence: Double,
    val reason: String
)
