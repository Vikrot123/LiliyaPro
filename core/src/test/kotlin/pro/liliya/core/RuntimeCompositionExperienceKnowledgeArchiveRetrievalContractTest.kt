package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.context.RuntimeContextMetadata
import pro.liliya.core.runtime.intelligence.context.RuntimeContextSnapshot
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceContext
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.selfmodel.RuntimeSelfModel

class RuntimeCompositionExperienceKnowledgeArchiveRetrievalContractTest {

    @Test
    fun archived_experience_derived_knowledge_must_disappear_from_shared_query() {
        val composition =
            DefaultRuntimeComposition()

        val result =
            composition
                .experienceKnowledgePipeline()
                .process(context())

        if (!result.experienceResult.decision.shouldRemember) {
            error(
                "test precondition failed: experience must be approved for remembering"
            )
        }

        val knowledge =
            assertNotNull(
                result.knowledgeResult?.knowledge
            )

        val lifecycleMemory =
            composition
                .knowledgeLifecycleComposition()
                .lifecycleMemory()

        val memory =
            lifecycleMemory.memory()

        val queryText =
            knowledge.statement

        assertEquals(
            RuntimeKnowledgeLifecycleState.ACTIVE,
            memory.getLifecycleState(knowledge)
        )

        assertTrue(
            memory.query(queryText).any { ranked ->
                ranked.result.node.knowledge == knowledge
            },
            "experience-derived knowledge must be visible before archive"
        )

        lifecycleMemory.revise(knowledge)

        assertEquals(
            RuntimeKnowledgeLifecycleState.REVIEW,
            memory.getLifecycleState(knowledge),
            "experience-derived knowledge must enter REVIEW before archive"
        )

        lifecycleMemory.archive(knowledge)

        assertEquals(
            RuntimeKnowledgeLifecycleState.ARCHIVED,
            memory.getLifecycleState(knowledge)
        )

        assertFalse(
            memory.query(queryText).any { ranked ->
                ranked.result.node.knowledge == knowledge
            },
            "archived experience-derived knowledge must be hidden from shared retrieval"
        )
    }

    private fun context(): RuntimeExperienceContext {
        return RuntimeExperienceContext(
            selfModel =
                RuntimeSelfModel(
                    snapshot =
                        RuntimeContextSnapshot(
                            runtimeState = "RUNNING",
                            activeServices =
                                listOf("runtime"),
                            timestamp = 1L
                        ),
                    metadata =
                        RuntimeContextMetadata(
                            runtimeVersion = "1",
                            recoveryAvailable = true,
                            diagnosticsAvailable = true
                        )
                ),
            meaning =
                RuntimeMeaningResult(
                    interpretation =
                        "archive retrievable experience knowledge",
                    confidence = 1.0,
                    significance =
                        RuntimeMeaningSignificance.WARNING,
                    generatedAt = 1L
                )
        )
    }
}
