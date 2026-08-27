package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection

import pro.liliya.core.runtime.intelligence.decision.explanation.RuntimeDecisionExplanation
import pro.liliya.core.runtime.intelligence.decision.reflection.RuntimeDecisionReflectionInsight

data class RuntimeAutonomousExecutionReflectionAnalysisResult(
    val evidence: RuntimeAutonomousExecutionReflectionEvidence,
    val explanation: RuntimeDecisionExplanation,
    val insight: RuntimeDecisionReflectionInsight
)
