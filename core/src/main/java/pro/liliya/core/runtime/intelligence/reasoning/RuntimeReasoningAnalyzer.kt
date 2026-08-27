package pro.liliya.core.runtime.intelligence.reasoning

import pro.liliya.core.runtime.intelligence.goal.RuntimeGoal
import pro.liliya.core.runtime.intelligence.planning.RuntimePlan

interface RuntimeReasoningAnalyzer {

    fun analyze(
        goal: RuntimeGoal,
        plan: RuntimePlan
    ): RuntimeReasoningAssessment
}
