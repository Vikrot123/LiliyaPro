package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.context.RuntimeContextMetadata
import pro.liliya.core.runtime.intelligence.context.RuntimeContextSnapshot
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceContext
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.selfmodel.RuntimeSelfModel

class RuntimeCompositionExperienceKnowledgeLifecycleCommitContractTest {

    private fun context(): RuntimeExperienceContext {
        return RuntimeExperienceContext(
            selfModel =
                RuntimeSelfModel(
                    snapshot =
                        RuntimeContextSnapshot(
                            runtimeState = "RUNNING",
                            activeServices = listOf("runtime"),
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
                        "experience-derived knowledge lifecycle commit",
                    confidence = 1.0,
                    significance =
                        RuntimeMeaningSignificance.WARNING,
                    generatedAt = 1L
                )
        )
    }

    @Test
    fun remembered_experience_knowledge_must_enter_composition_lifecycle_memory() {
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

        assertEquals(
            RuntimeKnowledgeLifecycleState.ACTIVE,
            lifecycleMemory
                .memory()
                .getLifecycleState(knowledge),
            "experience-derived knowledge must be committed into composition-owned lifecycle memory"
        )
    }
}
