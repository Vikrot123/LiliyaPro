package pro.liliya.core.runtime.intelligence.decision.reflection

import pro.liliya.core.runtime.intelligence.decision.explanation.RuntimeDecisionExplanation
import pro.liliya.core.runtime.intelligence.decision.reflection.history.RuntimeDecisionReflectionHistory
import pro.liliya.core.runtime.intelligence.decision.reflection.history.RuntimeDecisionReflectionRecord

class DefaultRuntimeDecisionReflectionRecorder(
    private val analyzer: RuntimeDecisionReflectionAnalyzer,
    private val history: RuntimeDecisionReflectionHistory
) : RuntimeDecisionReflectionRecorder {

    override fun analyzeAndRecord(
        explanation: RuntimeDecisionExplanation
    ): RuntimeDecisionReflectionRecord {

        return history.record(
            analyzer.analyze(
                explanation
            )
        )
    }
}
