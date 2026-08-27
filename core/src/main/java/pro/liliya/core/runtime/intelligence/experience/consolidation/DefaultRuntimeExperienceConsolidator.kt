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
                val latest =
                    experiences
                        .withIndex()
                        .maxWithOrNull(
                            compareBy<IndexedValue<RuntimeExperience>> {
                                it.value.createdAt
                            }.thenBy {
                                it.index
                            }
                        )!!
                        .value

                "Consolidated ${experiences.size} runtime experiences; latest: ${latest.description}"
            }

        return RuntimeExperienceConsolidation(
            summary = summary,
            processedCount = experiences.size,
            createdAt = System.currentTimeMillis()
        )
    }
}
