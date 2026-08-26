package pro.liliya.core.runtime.intelligence.experience.orchestration

import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceContext
import pro.liliya.core.runtime.intelligence.experience.orchestration.RuntimeExperienceKnowledgeOrchestrationResult
import pro.liliya.core.runtime.intelligence.experience.knowledge.RuntimeExperienceKnowledgePipeline
import pro.liliya.core.runtime.intelligence.experience.knowledge.RuntimeExperienceKnowledgePipelineResult

class DefaultRuntimeExperienceKnowledgeOrchestrator(
    private val pipeline: RuntimeExperienceKnowledgePipeline
) : RuntimeExperienceKnowledgeOrchestrator {

    override fun process(
        context: RuntimeExperienceContext
    ): RuntimeExperienceKnowledgeOrchestrationResult {
        return RuntimeExperienceKnowledgeOrchestrationResult(
            pipelineResult = pipeline.process(context)
        )
    }
}
