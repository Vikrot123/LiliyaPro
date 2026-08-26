package pro.liliya.core.runtime.intelligence.experience.knowledge

import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceContext

interface RuntimeExperienceKnowledgePipeline {

    fun process(
        context: RuntimeExperienceContext
    ): RuntimeExperienceKnowledgePipelineResult
}
