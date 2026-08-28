package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertSame
import kotlin.test.assertTrue
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionAutonomousCommittedKnowledgeFeedbackContractTest {

    @Test
    fun committed_autonomous_knowledge_is_available_to_next_intelligence_cycle() {
        val composition =
            DefaultRuntimeComposition()

        val pipeline =
            composition.autonomousIntelligenceCyclePipeline()

        val committedCycle =
            (1..8)
                .asSequence()
                .map { index ->
                    val source =
                        "v1.517-learning-$index"

                    pipeline.process(
                        source = source,
                        authority =
                            RuntimeActionAuthorityContext(
                                source = source,
                                level =
                                    RuntimeAuthorityLevel.SYSTEM
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
                    "autonomous cycle must produce committed experience-derived knowledge"
                )

        val committedKnowledge =
            committedCycle
                .executionCycle
                .postExecution
                .committedExperienceKnowledge
                ?.knowledgeResult
                ?.knowledge
                ?: error(
                    "committed autonomous experience must produce knowledge"
                )

        val lifecycleMemory =
            composition
                .knowledgeLifecycleComposition()
                .lifecycleMemory()

        val sharedMemory =
            lifecycleMemory.memory()

        assertTrue(
            sharedMemory
                .availableKnowledge()
                .any { knowledge ->
                    knowledge === committedKnowledge
                },
            "committed autonomous knowledge must enter shared knowledge memory"
        )

        /*
         * Make the feedback seam deterministic:
         * the next cognitive snapshot must have exactly the
         * committed autonomous knowledge under test available.
         */
        sharedMemory
            .availableKnowledge()
            .filter { knowledge ->
                knowledge !== committedKnowledge
            }
            .forEach { knowledge ->
                lifecycleMemory.revise(knowledge)
                lifecycleMemory.archive(knowledge)
            }

        val availableBeforeNextCycle =
            sharedMemory.availableKnowledge()

        assertTrue(
            availableBeforeNextCycle.isNotEmpty(),
            "committed autonomous knowledge must remain available"
        )

        assertTrue(
            availableBeforeNextCycle.all { knowledge ->
                knowledge === committedKnowledge
            },
            "test precondition requires committed autonomous knowledge to be the only available knowledge"
        )

        val nextSource =
            "v1.517-feedback-cycle"

        val nextCycle =
            pipeline.process(
                source = nextSource,
                authority =
                    RuntimeActionAuthorityContext(
                        source = nextSource,
                        level =
                            RuntimeAuthorityLevel.SYSTEM
                    )
            )

        val selection =
            nextCycle
                .intelligence
                .meaning
                .knowledgeSelection
                ?: error(
                    "next intelligence cycle must expose knowledge selection provenance"
                )

        assertSame(
            committedKnowledge,
            selection.knowledge,
            "next intelligence cycle must select previously committed autonomous knowledge"
        )

        assertTrue(
            nextCycle
                .intelligence
                .meaning
                .interpretation
                .contains(committedKnowledge.statement),
            "selected autonomous knowledge must enter next-cycle meaning interpretation"
        )
    }
}
