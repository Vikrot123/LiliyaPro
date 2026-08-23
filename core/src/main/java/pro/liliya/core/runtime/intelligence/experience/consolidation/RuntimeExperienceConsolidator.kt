package pro.liliya.core.runtime.intelligence.experience.consolidation

import pro.liliya.core.runtime.intelligence.experience.RuntimeExperience

interface RuntimeExperienceConsolidator {

    fun consolidate(
        experiences: List<RuntimeExperience>
    ): RuntimeExperienceConsolidation
}
