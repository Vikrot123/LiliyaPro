package pro.liliya.core.runtime.intelligence.decision.explanation.history

import pro.liliya.core.runtime.intelligence.decision.explanation.RuntimeDecisionExplanation

interface RuntimeDecisionExplanationHistory {

    fun record(
        explanation: RuntimeDecisionExplanation
    ): RuntimeDecisionExplanationRecord

    fun records(): List<RuntimeDecisionExplanationRecord>

    fun clear()
}
