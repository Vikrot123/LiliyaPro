package pro.liliya.core.runtime.intelligence.reasoning.orchestration

import pro.liliya.core.runtime.intelligence.goal.RuntimeGoal
import pro.liliya.core.runtime.intelligence.planning.RuntimePlan
import pro.liliya.core.runtime.intelligence.reasoning.RuntimeReasoningAssessment

data class RuntimeAutonomousReasoningResult(
    val goal: RuntimeGoal,
    val plan: RuntimePlan,
    val reasoning: RuntimeReasoningAssessment,
    val coherent: Boolean,
    val actionable: Boolean,
    val confidence: Double
)
