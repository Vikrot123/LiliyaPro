package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning

import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.feedback.RuntimeAutonomousExecutionFeedback

class DefaultRuntimeAutonomousExecutionExperienceNoveltyDeriver :
    RuntimeAutonomousExecutionExperienceNoveltyDeriver {

    override fun derive(
        feedback: RuntimeAutonomousExecutionFeedback
    ): RuntimeAutonomousExecutionExperienceNovelty {

        if (!feedback.actionAttempted) {
            return RuntimeAutonomousExecutionExperienceNovelty(
                state =
                    RuntimeAutonomousExecutionExperienceNoveltyState.ALREADY_REPRESENTED,
                novel =
                    false,
                actionAttempted =
                    false,
                actionSucceeded =
                    feedback.actionSucceeded,
                reason =
                    "No autonomous action produced new post-execution information"
            )
        }

        return RuntimeAutonomousExecutionExperienceNovelty(
            state =
                RuntimeAutonomousExecutionExperienceNoveltyState.NOVEL,
            novel =
                true,
            actionAttempted =
                true,
            actionSucceeded =
                feedback.actionSucceeded,
            reason =
                if (feedback.actionSucceeded == true) {
                    "Successful autonomous execution produced new post-execution information"
                } else {
                    "Failed autonomous execution produced new post-execution information"
                }
        )
    }
}
