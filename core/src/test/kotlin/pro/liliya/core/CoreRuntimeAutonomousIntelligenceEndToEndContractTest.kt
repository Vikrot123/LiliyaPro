package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceCommitState

class CoreRuntimeAutonomousIntelligenceEndToEndContractTest {

    @Test
    fun autonomous_intelligence_completes_learning_and_survives_public_runtime_restart() {
        CoreRuntime.stop()
        CoreRuntime.start()

        try {
            assertEquals(
                CoreRuntimeState.RUNNING,
                CoreRuntime.state()
            )

            val first =
                committedCycle(
                    prefix = "core-complete-before-restart"
                )

            val firstPostExecution =
                first.executionCycle.postExecution

            val firstCommit =
                assertNotNull(
                    firstPostExecution.commitResult,
                    "public autonomous cycle must reach experience commit boundary"
                )

            assertEquals(
                RuntimeAutonomousExecutionExperienceCommitState.COMMITTED,
                firstCommit.state,
                "public autonomous cycle must commit experience"
            )

            val firstKnowledgeBridge =
                assertNotNull(
                    firstPostExecution.committedExperienceKnowledge,
                    "committed public autonomous experience must enter knowledge pipeline"
                )

            assertSame(
                firstCommit.experience,
                firstKnowledgeBridge.experience,
                "knowledge pipeline must preserve committed experience identity"
            )

            val firstKnowledge =
                firstKnowledgeBridge.knowledgeResult.knowledge

            assertSame(
                first.intelligence,
                first.executionCycle.execution.intelligence,
                "execution must preserve intelligence identity"
            )

            CoreRuntime.stop()

            assertEquals(
                CoreRuntimeState.STOPPED,
                CoreRuntime.state()
            )

            CoreRuntime.start()

            assertEquals(
                CoreRuntimeState.RUNNING,
                CoreRuntime.state()
            )

            val second =
                committedCycle(
                    prefix = "core-complete-after-restart"
                )

            val secondPostExecution =
                second.executionCycle.postExecution

            val secondCommit =
                assertNotNull(
                    secondPostExecution.commitResult,
                    "autonomous learning must remain operational after runtime restart"
                )

            assertEquals(
                RuntimeAutonomousExecutionExperienceCommitState.COMMITTED,
                secondCommit.state,
                "post-restart autonomous cycle must commit experience"
            )

            val secondKnowledgeBridge =
                assertNotNull(
                    secondPostExecution.committedExperienceKnowledge,
                    "post-restart committed experience must enter knowledge pipeline"
                )

            assertSame(
                secondCommit.experience,
                secondKnowledgeBridge.experience,
                "post-restart knowledge pipeline must preserve committed experience identity"
            )

            val secondKnowledge =
                secondKnowledgeBridge.knowledgeResult.knowledge

            assertNotSame(
                firstCommit.experience,
                secondCommit.experience,
                "restart must not reuse previous autonomous experience identity"
            )

            assertNotSame(
                firstKnowledge,
                secondKnowledge,
                "restart must produce independent knowledge for independent experience"
            )

            assertSame(
                second.intelligence,
                second.executionCycle.execution.intelligence,
                "post-restart execution must preserve intelligence identity"
            )

            assertEquals(
                CoreRuntimeState.RUNNING,
                CoreRuntime.state()
            )
        } finally {
            CoreRuntime.stop()
        }

        assertEquals(
            CoreRuntimeState.STOPPED,
            CoreRuntime.state()
        )
    }

    private fun committedCycle(
        prefix: String
    ): pro.liliya.core.runtime.intelligence.autonomous.RuntimeAutonomousIntelligenceCyclePipelineResult {

        return (1..16)
            .asSequence()
            .map { index ->
                val source =
                    "$prefix-$index"

                CoreRuntime.processAutonomousIntelligenceCycle(
                    source = source,
                    authority =
                        RuntimeActionAuthorityContext(
                            source = source,
                            level = RuntimeAuthorityLevel.SYSTEM
                        )
                )
            }
            .firstOrNull { result ->
                result
                    .executionCycle
                    .postExecution
                    .committedExperienceKnowledge != null
            }
            ?: error(
                "public autonomous runtime must eventually produce committed experience-derived knowledge"
            )
    }
}
