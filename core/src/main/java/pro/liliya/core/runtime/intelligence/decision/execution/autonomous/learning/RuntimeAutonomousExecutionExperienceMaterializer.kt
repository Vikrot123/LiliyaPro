package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning

import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection.RuntimeAutonomousExecutionReflectionAnalysisResult

interface RuntimeAutonomousExecutionExperienceMaterializer {

    fun materialize(
        analysis: RuntimeAutonomousExecutionReflectionAnalysisResult,
        novelty: RuntimeAutonomousExecutionExperienceNovelty
    ): RuntimeAutonomousExecutionExperienceMaterialization?
}
