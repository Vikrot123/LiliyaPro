package pro.liliya.core.runtime.intelligence.experience.knowledge

import pro.liliya.core.runtime.intelligence.experience.pipeline.RuntimeExperiencePipelineResult
import pro.liliya.core.runtime.intelligence.knowledge.pipeline.RuntimeKnowledgePipelineResult

data class RuntimeExperienceKnowledgePipelineResult(
    val experienceResult: RuntimeExperiencePipelineResult,
    val consolidation: pro.liliya.core.runtime.intelligence.experience.consolidation.RuntimeExperienceConsolidation?,
    val knowledgeResult: RuntimeKnowledgePipelineResult?
)
