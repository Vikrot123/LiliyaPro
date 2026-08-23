package pro.liliya.core.runtime.intelligence.experience.decision

import pro.liliya.core.runtime.intelligence.experience.RuntimeExperience

interface RuntimeExperienceDecisionEngine {

    fun decide(
        experience: RuntimeExperience
    ): RuntimeExperienceDecision
}
