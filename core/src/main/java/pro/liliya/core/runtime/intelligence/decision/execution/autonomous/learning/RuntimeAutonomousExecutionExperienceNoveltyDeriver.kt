package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning

import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.feedback.RuntimeAutonomousExecutionFeedback

interface RuntimeAutonomousExecutionExperienceNoveltyDeriver {

    fun derive(
        feedback: RuntimeAutonomousExecutionFeedback
    ): RuntimeAutonomousExecutionExperienceNovelty
}
