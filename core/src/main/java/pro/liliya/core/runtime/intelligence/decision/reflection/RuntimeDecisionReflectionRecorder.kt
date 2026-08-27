package pro.liliya.core.runtime.intelligence.decision.reflection

import pro.liliya.core.runtime.intelligence.decision.explanation.RuntimeDecisionExplanation
import pro.liliya.core.runtime.intelligence.decision.reflection.history.RuntimeDecisionReflectionRecord

interface RuntimeDecisionReflectionRecorder {

    fun analyzeAndRecord(
        explanation: RuntimeDecisionExplanation
    ): RuntimeDecisionReflectionRecord
}
