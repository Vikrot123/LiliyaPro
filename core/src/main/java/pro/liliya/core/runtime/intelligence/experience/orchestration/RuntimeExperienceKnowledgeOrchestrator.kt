package pro.liliya.core.runtime.intelligence.experience.orchestration

import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceContext
import pro.liliya.core.runtime.intelligence.experience.orchestration.RuntimeExperienceKnowledgeOrchestrationResult
import pro.liliya.core.runtime.intelligence.experience.knowledge.RuntimeExperienceKnowledgePipelineResult

interface RuntimeExperienceKnowledgeOrchestrator {

    fun process(
        context: RuntimeExperienceContext
    ): RuntimeExperienceKnowledgeOrchestrationResult
}
