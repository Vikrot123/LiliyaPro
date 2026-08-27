package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
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

class RuntimeCompositionExperienceKnowledgeRetrievalContractTest {

    @Test
    fun experience_derived_knowledge_must_be_retrievable_from_shared_memory() {
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

        val memory =
            composition
                .knowledgeLifecycleComposition()
                .lifecycleMemory()
                .memory()

        assertEquals(
            RuntimeKnowledgeLifecycleState.ACTIVE,
            memory.getLifecycleState(knowledge),
            "experience-derived knowledge must be ACTIVE before retrieval"
        )

        val queryText =
            knowledge.statement

        val results =
            memory.query(queryText)

        assertTrue(
            results.any { ranked ->
                ranked.result.node.knowledge == knowledge
            },
            "experience-derived knowledge must be visible through the shared knowledge query path"
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
                        "retrievable experience derived knowledge",
                    confidence = 1.0,
                    significance =
                        RuntimeMeaningSignificance.WARNING,
                    generatedAt = 1L
                )
        )
    }
}
