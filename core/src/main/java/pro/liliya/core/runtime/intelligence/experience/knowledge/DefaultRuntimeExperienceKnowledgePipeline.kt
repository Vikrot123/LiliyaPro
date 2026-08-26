package pro.liliya.core.runtime.intelligence.experience.knowledge

import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceContext
import pro.liliya.core.runtime.intelligence.experience.consolidation.RuntimeExperienceConsolidation
import pro.liliya.core.runtime.intelligence.experience.consolidation.RuntimeExperienceConsolidator
import pro.liliya.core.runtime.intelligence.experience.pipeline.RuntimeExperiencePipeline
import pro.liliya.core.runtime.intelligence.knowledge.pipeline.RuntimeKnowledgePipeline

class DefaultRuntimeExperienceKnowledgePipeline(
    private val experiencePipeline: RuntimeExperiencePipeline,
    private val experienceConsolidator: RuntimeExperienceConsolidator,
    private val knowledgePipeline: RuntimeKnowledgePipeline
) : RuntimeExperienceKnowledgePipeline {

    override fun process(
        context: RuntimeExperienceContext
    ): RuntimeExperienceKnowledgePipelineResult {
        val experienceResult = experiencePipeline.process(context)

        if (!experienceResult.decision.shouldRemember) {
            return RuntimeExperienceKnowledgePipelineResult(
                experienceResult = experienceResult,
                consolidation = null,
                knowledgeResult = null
            )
        }

        val consolidation: RuntimeExperienceConsolidation =
            experienceConsolidator.consolidate(
                listOf(experienceResult.experience)
            )

        val knowledgeResult = knowledgePipeline.process(consolidation)

        return RuntimeExperienceKnowledgePipelineResult(
            experienceResult = experienceResult,
            consolidation = consolidation,
            knowledgeResult = knowledgeResult
        )
    }
}
