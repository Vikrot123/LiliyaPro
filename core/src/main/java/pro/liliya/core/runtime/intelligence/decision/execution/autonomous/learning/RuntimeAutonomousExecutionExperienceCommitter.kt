package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning

interface RuntimeAutonomousExecutionExperienceCommitter {

    fun commit(
        decision: RuntimeAutonomousExecutionExperienceCommitDecision
    ): RuntimeAutonomousExecutionExperienceCommitResult
}
