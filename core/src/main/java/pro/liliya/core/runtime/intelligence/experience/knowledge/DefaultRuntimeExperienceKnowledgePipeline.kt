package pro.liliya.core.runtime.intelligence.experience.knowledge

import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceContext
import pro.liliya.core.runtime.intelligence.experience.consolidation.RuntimeExperienceConsolidation
import pro.liliya.core.runtime.intelligence.experience.consolidation.RuntimeExperienceConsolidator
import pro.liliya.core.runtime.intelligence.experience.pipeline.RuntimeExperiencePipeline
import pro.liliya.core.runtime.intelligence.experience.store.RuntimeExperienceStore
import pro.liliya.core.runtime.intelligence.knowledge.pipeline.RuntimeKnowledgePipeline
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.integration.RuntimeKnowledgeLifecycleMemory

class DefaultRuntimeExperienceKnowledgePipeline(
    private val experiencePipeline: RuntimeExperiencePipeline,
    private val experienceConsolidator: RuntimeExperienceConsolidator,
    private val knowledgePipeline: RuntimeKnowledgePipeline,
    private val experienceStore: RuntimeExperienceStore? = null,
    private val knowledgeLifecycleMemory:
        RuntimeKnowledgeLifecycleMemory? = null
) : RuntimeExperienceKnowledgePipeline {

    override fun process(
        context: RuntimeExperienceContext
    ): RuntimeExperienceKnowledgePipelineResult {
        val experienceResult =
            experiencePipeline.process(context)

        if (!experienceResult.decision.shouldRemember) {
            return RuntimeExperienceKnowledgePipelineResult(
                experienceResult = experienceResult,
                consolidation = null,
                knowledgeResult = null
            )
        }

        try {
            val consolidation: RuntimeExperienceConsolidation =
                experienceConsolidator.consolidate(
                    listOf(experienceResult.experience)
                )

            val knowledgeResult =
                knowledgePipeline.process(consolidation)

            knowledgeLifecycleMemory?.create(
                knowledgeResult.knowledge
            )

            return RuntimeExperienceKnowledgePipelineResult(
                experienceResult = experienceResult,
                consolidation = consolidation,
                knowledgeResult = knowledgeResult
            )
        } catch (error: Throwable) {
            try {
                experienceStore?.remove(
                    experienceResult.experience
                )
            } catch (rollbackError: Throwable) {
                error.addSuppressed(
                    rollbackError
                )
            }

            throw error
        }
    }
}
