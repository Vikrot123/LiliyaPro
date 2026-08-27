package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.context.RuntimeContextMetadata
import pro.liliya.core.runtime.intelligence.context.RuntimeContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.source.KnowledgeCognitiveContextSource
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceContext
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.selfmodel.RuntimeSelfModel

class RuntimeCompositionExperienceKnowledgeCognitiveContextContractTest {

    @Test
    fun active_experience_knowledge_must_enter_working_cognitive_context() {
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

        val cognitiveSnapshot =
            composition
                .cognitiveContextComposition()
                .context()
                .snapshot(
                    CognitiveContextType.WORKING
                )

        val available =
            cognitiveSnapshot.values[
                KnowledgeCognitiveContextSource
                    .AVAILABLE_KNOWLEDGE_KEY
            ] as? List<*>

        assertNotNull(
            available,
            "WORKING cognitive context must expose available knowledge"
        )

        assertTrue(
            available.any { candidate ->
                candidate == knowledge
            },
            "experience-derived ACTIVE knowledge must enter cognitive context"
        )

        assertTrue(
            available.all { candidate ->
                candidate is RuntimeKnowledge
            },
            "cognitive knowledge snapshot must contain RuntimeKnowledge values"
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
                        "knowledge enters cognitive context",
                    confidence = 1.0,
                    significance =
                        RuntimeMeaningSignificance.WARNING,
                    generatedAt = 1L
                )
        )
    }
}
