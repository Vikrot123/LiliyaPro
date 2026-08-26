package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

import pro.liliya.core.runtime.intelligence.experience.RuntimeExperience
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceImportance
import pro.liliya.core.runtime.intelligence.experience.composition.DefaultRuntimeExperienceComposition
import pro.liliya.core.runtime.intelligence.experience.consolidation.RuntimeExperienceConsolidation
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class DefaultRuntimeExperienceCompositionConsolidationContractTest {

    private fun experience(text: String): RuntimeExperience {
        return RuntimeExperience(
            description = text,
            meaning = RuntimeMeaningResult(
                interpretation = text,
                confidence = 1.0,
                significance = RuntimeMeaningSignificance.WARNING,
                generatedAt = 1L
            ),
            importance = RuntimeExperienceImportance.MEDIUM,
            createdAt = 1L
        )
    }

    @Test
    fun composition_exposes_stable_consolidator() {
        val composition = DefaultRuntimeExperienceComposition()

        val consolidator = composition.experienceConsolidator()

        assertSame(
            consolidator,
            composition.experienceConsolidator()
        )
    }

    @Test
    fun composition_consolidator_can_process_composition_store_state() {
        val composition = DefaultRuntimeExperienceComposition()
        val store = composition.experienceStore()

        store.append(experience("first"))
        store.append(experience("second"))

        val result: RuntimeExperienceConsolidation =
            composition.experienceConsolidator()
                .consolidate(store.experiences())

        assertEquals(
            2,
            result.processedCount
        )

        assertEquals(
            "Consolidated 2 runtime experiences",
            result.summary
        )
    }
}
