package pro.liliya.core.runtime.intelligence.decision.explanation

import pro.liliya.core.runtime.intelligence.decision.RuntimeDecision

interface RuntimeDecisionExplainer {

    fun explain(
        decision: RuntimeDecision
    ): RuntimeDecisionExplanation
}
