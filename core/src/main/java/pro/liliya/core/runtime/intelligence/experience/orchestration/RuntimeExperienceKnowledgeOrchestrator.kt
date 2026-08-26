package pro.liliya.core.runtime.intelligence.experience.orchestration

import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceContext
import pro.liliya.core.runtime.intelligence.experience.knowledge.RuntimeExperienceKnowledgePipeline
import pro.liliya.core.runtime.intelligence.experience.knowledge.RuntimeExperienceKnowledgePipelineResult

interface RuntimeExperienceKnowledgeOrchestrator {

    fun process(
        context: RuntimeExperienceContext
    ): RuntimeExperienceKnowledgePipelineResult
}
