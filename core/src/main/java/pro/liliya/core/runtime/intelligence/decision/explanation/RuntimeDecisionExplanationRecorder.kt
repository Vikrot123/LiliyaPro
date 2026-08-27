package pro.liliya.core.runtime.intelligence.decision.explanation

import pro.liliya.core.runtime.intelligence.decision.RuntimeDecision
import pro.liliya.core.runtime.intelligence.decision.explanation.history.RuntimeDecisionExplanationRecord

interface RuntimeDecisionExplanationRecorder {

    fun explainAndRecord(
        decision: RuntimeDecision
    ): RuntimeDecisionExplanationRecord
}
