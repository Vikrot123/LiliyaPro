package pro.liliya.core.runtime.intelligence.knowledge.pipeline

import pro.liliya.core.runtime.intelligence.experience.consolidation.RuntimeExperienceConsolidation
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeExtractor

class DefaultRuntimeKnowledgePipeline(
    private val knowledgeExtractor: RuntimeKnowledgeExtractor
) : RuntimeKnowledgePipeline {

    override fun process(
        consolidation: RuntimeExperienceConsolidation
    ): RuntimeKnowledgePipelineResult {
        val knowledge = knowledgeExtractor.extract(consolidation)

        return RuntimeKnowledgePipelineResult(
            knowledge = knowledge
        )
    }
}
