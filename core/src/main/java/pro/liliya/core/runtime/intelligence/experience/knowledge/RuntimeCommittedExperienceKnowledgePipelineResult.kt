package pro.liliya.core.runtime.intelligence.experience.knowledge

import pro.liliya.core.runtime.intelligence.experience.RuntimeExperience
import pro.liliya.core.runtime.intelligence.experience.consolidation.RuntimeExperienceConsolidation
import pro.liliya.core.runtime.intelligence.knowledge.pipeline.RuntimeKnowledgePipelineResult

data class RuntimeCommittedExperienceKnowledgePipelineResult(
    val experience: RuntimeExperience,
    val consolidation: RuntimeExperienceConsolidation,
    val knowledgeResult: RuntimeKnowledgePipelineResult
)
