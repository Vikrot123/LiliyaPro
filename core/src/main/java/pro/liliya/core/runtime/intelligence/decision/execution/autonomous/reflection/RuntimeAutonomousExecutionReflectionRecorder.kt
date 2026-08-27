package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection

import pro.liliya.core.runtime.intelligence.decision.reflection.history.RuntimeDecisionReflectionRecord

interface RuntimeAutonomousExecutionReflectionRecorder {

    fun record(
        analysis: RuntimeAutonomousExecutionReflectionAnalysisResult
    ): RuntimeDecisionReflectionRecord?
}
