package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning

import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection.RuntimeAutonomousExecutionReflectionAnalysisResult
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceContext

class DefaultRuntimeAutonomousExecutionExperienceContextDeriver :
    RuntimeAutonomousExecutionExperienceContextDeriver {

    override fun derive(
        analysis: RuntimeAutonomousExecutionReflectionAnalysisResult
    ): RuntimeExperienceContext? {

        if (!analysis.evidence.consistent) {
            return null
        }

        val intelligence =
            analysis
                .evidence
                .evaluation
                .executionResult
                .intelligence

        return RuntimeExperienceContext(
            selfModel =
                intelligence.selfModel,
            meaning =
                intelligence.meaning
        )
    }
}
