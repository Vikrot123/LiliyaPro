package pro.liliya.interaction

import pro.liliya.core.CoreRuntime
import pro.liliya.core.CoreRuntimeState
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceCommitState

internal class CoreRuntimeInteractionPort(
    private val runtime: CoreRuntime
) : LiliyaInteractionPort,
    LiliyaInteractionLifecyclePort {

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

    override fun start() {
        runtime.start()
    }

    override fun stop() {
        runtime.stop()
    }

    override fun state(): LiliyaInteractionRuntimeState {
        return runtime
            .state()
            .toInteractionState()
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

    private fun CoreRuntimeState.toInteractionState():
        LiliyaInteractionRuntimeState {
        return when (this) {
            CoreRuntimeState.STOPPED ->
                LiliyaInteractionRuntimeState.STOPPED

            CoreRuntimeState.STARTING ->
                LiliyaInteractionRuntimeState.STARTING

            CoreRuntimeState.RUNNING ->
                LiliyaInteractionRuntimeState.RUNNING

            CoreRuntimeState.FAILED ->
                LiliyaInteractionRuntimeState.FAILED
        }
    }
}
