package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning

import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection.RuntimeAutonomousExecutionReflectionAnalysisResult

class DefaultRuntimeAutonomousExecutionExperienceMaterializer :
    RuntimeAutonomousExecutionExperienceMaterializer {

    override fun materialize(
        analysis: RuntimeAutonomousExecutionReflectionAnalysisResult,
        novelty: RuntimeAutonomousExecutionExperienceNovelty
    ): RuntimeAutonomousExecutionExperienceMaterialization? {

        if (!analysis.evidence.consistent) {
            return null
        }

        val feedback =
            analysis.evidence.feedback

        val noveltyConsistent =
            novelty.actionAttempted ==
                feedback.actionAttempted &&
                novelty.actionSucceeded ==
                    feedback.actionSucceeded

        if (!noveltyConsistent) {
            return null
        }

        if (!novelty.novel) {
            return null
        }

        val description =
            when (feedback.actionSucceeded) {
                true ->
                    "Autonomous action ${feedback.command} succeeded"

                false ->
                    "Autonomous action ${feedback.command} failed"

                null ->
                    "Autonomous action ${feedback.command} produced post-execution information"
            }

        return RuntimeAutonomousExecutionExperienceMaterialization(
            state =
                RuntimeAutonomousExecutionExperienceMaterializationState.MATERIALIZED,
            analysis =
                analysis,
            novelty =
                novelty,
            command =
                feedback.command,
            actionSucceeded =
                feedback.actionSucceeded,
            previousRuntimeState =
                feedback.previousRuntimeState,
            currentRuntimeState =
                feedback.currentRuntimeState,
            message =
                feedback.message,
            description =
                description
        )
    }
}
