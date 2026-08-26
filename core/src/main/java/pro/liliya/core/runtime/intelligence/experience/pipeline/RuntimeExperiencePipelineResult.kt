package pro.liliya.core.runtime.intelligence.experience.pipeline

import pro.liliya.core.runtime.intelligence.experience.RuntimeExperience
import pro.liliya.core.runtime.intelligence.experience.decision.RuntimeExperienceDecision

data class RuntimeExperiencePipelineResult(
    val experience: RuntimeExperience,
    val decision: RuntimeExperienceDecision
)
