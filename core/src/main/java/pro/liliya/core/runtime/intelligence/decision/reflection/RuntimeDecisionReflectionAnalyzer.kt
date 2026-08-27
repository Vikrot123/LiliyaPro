package pro.liliya.core.runtime.intelligence.decision.reflection

import pro.liliya.core.runtime.intelligence.decision.explanation.RuntimeDecisionExplanation

interface RuntimeDecisionReflectionAnalyzer {

    fun analyze(
        explanation: RuntimeDecisionExplanation
    ): RuntimeDecisionReflectionInsight
}
