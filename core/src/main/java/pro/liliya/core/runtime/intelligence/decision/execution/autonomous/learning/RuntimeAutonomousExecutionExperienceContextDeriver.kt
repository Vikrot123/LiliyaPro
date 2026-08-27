package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning

import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection.RuntimeAutonomousExecutionReflectionAnalysisResult
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceContext

interface RuntimeAutonomousExecutionExperienceContextDeriver {

    fun derive(
        analysis: RuntimeAutonomousExecutionReflectionAnalysisResult
    ): RuntimeExperienceContext?
}
