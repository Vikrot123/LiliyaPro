package pro.liliya.core.runtime.intelligence.experience.pipeline

import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceContext
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceEngine
import pro.liliya.core.runtime.intelligence.experience.decision.RuntimeExperienceDecisionEngine
import pro.liliya.core.runtime.intelligence.experience.store.RuntimeExperienceStore

class DefaultRuntimeExperiencePipeline(
    private val experienceEngine: RuntimeExperienceEngine,
    private val decisionEngine: RuntimeExperienceDecisionEngine,
    private val experienceStore: RuntimeExperienceStore
) : RuntimeExperiencePipeline {

    override fun process(
        context: RuntimeExperienceContext
    ): RuntimeExperiencePipelineResult {
        val experience = experienceEngine.createExperience(context)
        val decision = decisionEngine.decide(experience)

        if (decision.shouldRemember) {
            experienceStore.append(experience)
        }

        return RuntimeExperiencePipelineResult(
            experience = experience,
            decision = decision
        )
    }
}
