package pro.liliya.core.runtime.intelligence.experience.knowledge

import pro.liliya.core.runtime.intelligence.experience.RuntimeExperience
import pro.liliya.core.runtime.intelligence.experience.consolidation.RuntimeExperienceConsolidator
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.integration.RuntimeKnowledgeLifecycleMemory
import pro.liliya.core.runtime.intelligence.knowledge.pipeline.RuntimeKnowledgePipeline

class DefaultRuntimeCommittedExperienceKnowledgePipeline(
    private val experienceConsolidator: RuntimeExperienceConsolidator,
    private val knowledgePipeline: RuntimeKnowledgePipeline,
    private val knowledgeLifecycleMemory: RuntimeKnowledgeLifecycleMemory
) : RuntimeCommittedExperienceKnowledgePipeline {

    override fun process(
        experience: RuntimeExperience
    ): RuntimeCommittedExperienceKnowledgePipelineResult {

        val consolidation =
            experienceConsolidator.consolidate(
                listOf(experience)
            )

        val knowledgeResult =
            knowledgePipeline.process(
                consolidation
            )

        knowledgeLifecycleMemory.create(
            knowledgeResult.knowledge
        )

        return RuntimeCommittedExperienceKnowledgePipelineResult(
            experience = experience,
            consolidation = consolidation,
            knowledgeResult = knowledgeResult
        )
    }
}
