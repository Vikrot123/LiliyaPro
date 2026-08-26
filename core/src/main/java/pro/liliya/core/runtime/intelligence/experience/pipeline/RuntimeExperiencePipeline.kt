package pro.liliya.core.runtime.intelligence.experience.pipeline

import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceContext

interface RuntimeExperiencePipeline {
    fun process(
        context: RuntimeExperienceContext
    ): RuntimeExperiencePipelineResult
}
