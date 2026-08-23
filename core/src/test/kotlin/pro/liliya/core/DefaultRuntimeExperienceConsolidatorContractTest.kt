package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperience
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceImportance
import pro.liliya.core.runtime.intelligence.experience.consolidation.DefaultRuntimeExperienceConsolidator
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class DefaultRuntimeExperienceConsolidatorContractTest {

    private fun experience(): RuntimeExperience {
        return RuntimeExperience(
            description = "experience",
            meaning = RuntimeMeaningResult(
                interpretation = "meaning",
                confidence = 1.0,
                significance = RuntimeMeaningSignificance.STABLE,
                generatedAt = 1L
            ),
            importance = RuntimeExperienceImportance.LOW,
            createdAt = 1L
        )
    }

    @Test
    fun empty_experience_list_creates_empty_consolidation() {

        val result =
            DefaultRuntimeExperienceConsolidator()
                .consolidate(emptyList())

        assertEquals(
            0,
            result.processedCount
        )
    }

    @Test
    fun multiple_experiences_are_processed() {

        val result =
            DefaultRuntimeExperienceConsolidator()
                .consolidate(
                    listOf(
                        experience(),
                        experience()
                    )
                )

        assertEquals(
            2,
            result.processedCount
        )
    }
}
