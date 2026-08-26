package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceContext
import pro.liliya.core.runtime.intelligence.context.RuntimeContextMetadata
import pro.liliya.core.runtime.intelligence.context.RuntimeContextSnapshot
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.selfmodel.RuntimeSelfModel

class RuntimeCompositionExperienceKnowledgeEndToEndContractTest {

    private fun context(): RuntimeExperienceContext {
        return RuntimeExperienceContext(
            selfModel = RuntimeSelfModel(
                snapshot = RuntimeContextSnapshot(
                    runtimeState = "RUNNING",
                    activeServices = listOf("runtime"),
                    timestamp = 1L
                ),
                metadata = RuntimeContextMetadata(
                    runtimeVersion = "1",
                    recoveryAvailable = true,
                    diagnosticsAvailable = true
                )
            ),
            meaning = RuntimeMeaningResult(
                interpretation = "end-to-end runtime meaning",
                confidence = 1.0,
                significance = RuntimeMeaningSignificance.WARNING,
                generatedAt = 1L
            )
        )
    }

    @Test
    fun root_experience_knowledge_pipeline_is_reachable() {
        val composition = DefaultRuntimeComposition()

        val pipeline = composition.experienceKnowledgePipeline()

        assertNotNull(pipeline)
        assertSame(
            pipeline,
            composition.experienceKnowledgePipeline()
        )
    }

    @Test
    fun root_exposes_the_experience_knowledge_pipeline_through_composed_components() {
        val composition = DefaultRuntimeComposition()

        val result = composition
            .experienceKnowledgePipeline()
            .process(context())

        assertNotNull(result.experienceResult)
        assertNotNull(result.experienceResult.experience)
        assertNotNull(result.experienceResult.decision)

        if (result.experienceResult.decision.shouldRemember) {
            assertNotNull(result.consolidation)
            assertNotNull(result.knowledgeResult)
            assertNotNull(result.knowledgeResult?.knowledge)

            assertEquals(
                result.consolidation?.processedCount,
                result.knowledgeResult?.knowledge?.let {
                    result.consolidation?.processedCount
                }
            )
        } else {
            assertEquals(null, result.consolidation)
            assertEquals(null, result.knowledgeResult)
        }
    }
}
