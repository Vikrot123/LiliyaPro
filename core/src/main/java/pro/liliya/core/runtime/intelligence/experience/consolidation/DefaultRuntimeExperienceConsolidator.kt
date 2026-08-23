package pro.liliya.core.runtime.intelligence.experience.consolidation

import pro.liliya.core.runtime.intelligence.experience.RuntimeExperience

class DefaultRuntimeExperienceConsolidator :
    RuntimeExperienceConsolidator {

    override fun consolidate(
        experiences: List<RuntimeExperience>
    ): RuntimeExperienceConsolidation {

        val summary =
            if (experiences.isEmpty()) {
                "No experiences available for consolidation"
            } else {
                "Consolidated ${experiences.size} runtime experiences"
            }

        return RuntimeExperienceConsolidation(
            summary = summary,
            processedCount = experiences.size,
            createdAt = System.currentTimeMillis()
        )
    }
}
