package pro.liliya.core.runtime.intelligence.experience.knowledge

import pro.liliya.core.runtime.intelligence.experience.RuntimeExperience

interface RuntimeCommittedExperienceKnowledgePipeline {
    fun process(
        experience: RuntimeExperience
    ): RuntimeCommittedExperienceKnowledgePipelineResult
}
