package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection

import pro.liliya.core.runtime.intelligence.decision.reflection.history.RuntimeDecisionReflectionHistory
import pro.liliya.core.runtime.intelligence.decision.reflection.history.RuntimeDecisionReflectionRecord

class DefaultRuntimeAutonomousExecutionReflectionRecorder(
    private val history:
        RuntimeDecisionReflectionHistory
) : RuntimeAutonomousExecutionReflectionRecorder {

    override fun record(
        analysis: RuntimeAutonomousExecutionReflectionAnalysisResult
    ): RuntimeDecisionReflectionRecord? {

        if (!analysis.evidence.consistent) {
            return null
        }

        return history.record(
            analysis.insight
        )
    }
}
