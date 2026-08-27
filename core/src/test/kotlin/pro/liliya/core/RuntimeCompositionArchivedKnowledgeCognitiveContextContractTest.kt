package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.context.RuntimeContextMetadata
import pro.liliya.core.runtime.intelligence.context.RuntimeContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.source.KnowledgeCognitiveContextSource
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceContext
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.selfmodel.RuntimeSelfModel

class RuntimeCompositionArchivedKnowledgeCognitiveContextContractTest {

    @Test
    fun archived_experience_knowledge_must_be_hidden_from_working_cognitive_context() {
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

        fun availableKnowledge(): List<*> {
            val snapshot =
                composition
                    .cognitiveContextComposition()
                    .context()
                    .snapshot(
                        CognitiveContextType.WORKING
                    )

            return assertNotNull(
                snapshot.values[
                    KnowledgeCognitiveContextSource
                        .AVAILABLE_KNOWLEDGE_KEY
                ] as? List<*>
            )
        }

        assertTrue(
            availableKnowledge().any { candidate ->
                candidate == knowledge
            },
            "ACTIVE knowledge must be visible before archive"
        )

        lifecycleMemory.revise(knowledge)
        lifecycleMemory.archive(knowledge)

        assertFalse(
            availableKnowledge().any { candidate ->
                candidate == knowledge
            },
            "ARCHIVED knowledge must not enter WORKING cognitive context"
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
                        "archived knowledge cognitive filtering",
                    confidence = 1.0,
                    significance =
                        RuntimeMeaningSignificance.WARNING,
                    generatedAt = 1L
                )
        )
    }
}
