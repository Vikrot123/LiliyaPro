package pro.liliya.core.runtime.intelligence.knowledge.pipeline

import pro.liliya.core.runtime.intelligence.experience.consolidation.RuntimeExperienceConsolidation

interface RuntimeKnowledgePipeline {

    fun process(
        consolidation: RuntimeExperienceConsolidation
    ): RuntimeKnowledgePipelineResult
}
