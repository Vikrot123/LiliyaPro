package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperience
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceImportance
import pro.liliya.core.runtime.intelligence.experience.consolidation.DefaultRuntimeExperienceConsolidator
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class DefaultRuntimeExperienceConsolidatorSemanticContentContractTest {

    @Test
    fun consolidation_must_preserve_bounded_semantic_content_from_latest_experience() {
        val older =
            experience(
                description = "older runtime recovery experience",
                createdAt = 10L
            )

        val newer =
            experience(
                description = "latest runtime stability experience",
                createdAt = 20L
            )

        val result =
            DefaultRuntimeExperienceConsolidator()
                .consolidate(
                    listOf(
                        older,
                        newer
                    )
                )

        assertEquals(
            2,
            result.processedCount
        )

        assertTrue(
            result.summary.contains(
                newer.description
            ),
            "consolidation must preserve semantic content from the latest experience"
        )

        assertFalse(
            result.summary.contains(
                older.description
            ),
            "consolidation must remain bounded and not concatenate the full experience history"
        )
    }

    private fun experience(
        description: String,
        createdAt: Long
    ): RuntimeExperience {
        return RuntimeExperience(
            description = description,
            meaning =
                RuntimeMeaningResult(
                    interpretation = description,
                    confidence = 0.9,
                    significance =
                        RuntimeMeaningSignificance.STABLE,
                    generatedAt = createdAt
                ),
            importance =
                RuntimeExperienceImportance.HIGH,
            createdAt = createdAt
        )
    }
}
