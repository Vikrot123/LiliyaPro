package pro.liliya.interaction

import pro.liliya.core.CoreRuntime
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceCommitState

class CoreRuntimeInteractionPort(
    private val runtime: CoreRuntime
) : LiliyaInteractionPort {

    override fun process(
        source: String,
        authority: LiliyaInteractionAuthority
    ): LiliyaInteractionResult {
        val result =
            runtime.processAutonomousIntelligenceCycle(
                source = source,
                authority =
                    RuntimeActionAuthorityContext(
                        source = source,
                        level = authority.toRuntimeAuthority()
                    )
            )

        val postExecution =
            result.executionCycle.postExecution

        return LiliyaInteractionResult(
            source = source,
            interpretation =
                result.intelligence.meaning.interpretation,
            confidence =
                result.intelligence.meaning.confidence,
            experienceCommitted =
                postExecution.commitResult?.state ==
                    RuntimeAutonomousExecutionExperienceCommitState.COMMITTED,
            knowledgeProduced =
                postExecution.committedExperienceKnowledge != null
        )
    }

    private fun LiliyaInteractionAuthority.toRuntimeAuthority():
        RuntimeAuthorityLevel {
        return when (this) {
            LiliyaInteractionAuthority.INTERNAL ->
                RuntimeAuthorityLevel.INTERNAL

            LiliyaInteractionAuthority.SYSTEM ->
                RuntimeAuthorityLevel.SYSTEM

            LiliyaInteractionAuthority.USER ->
                RuntimeAuthorityLevel.USER

            LiliyaInteractionAuthority.UNKNOWN ->
                RuntimeAuthorityLevel.UNKNOWN
        }
    }
}
