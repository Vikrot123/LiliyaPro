package pro.liliya.core.runtime.intelligence.decision.explanation

import pro.liliya.core.runtime.intelligence.decision.RuntimeDecision
import pro.liliya.core.runtime.intelligence.decision.explanation.history.RuntimeDecisionExplanationHistory
import pro.liliya.core.runtime.intelligence.decision.explanation.history.RuntimeDecisionExplanationRecord

class DefaultRuntimeDecisionExplanationRecorder(
    private val explainer: RuntimeDecisionExplainer,
    private val history: RuntimeDecisionExplanationHistory
) : RuntimeDecisionExplanationRecorder {

    override fun explainAndRecord(
        decision: RuntimeDecision
    ): RuntimeDecisionExplanationRecord {

        return history.record(
            explainer.explain(decision)
        )
    }
}
