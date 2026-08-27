package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning

import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection.RuntimeAutonomousExecutionReflectionAnalysisResult

interface RuntimeAutonomousExecutionExperienceLearningDecisionEngine {

    fun decide(
        analysis: RuntimeAutonomousExecutionReflectionAnalysisResult
    ): RuntimeAutonomousExecutionExperienceLearningDecision
}
