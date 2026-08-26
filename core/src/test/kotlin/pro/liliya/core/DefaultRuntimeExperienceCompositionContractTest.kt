package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertEquals

import pro.liliya.core.runtime.intelligence.experience.RuntimeExperience
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceImportance
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

import pro.liliya.core.runtime.intelligence.experience.composition.DefaultRuntimeExperienceComposition

class DefaultRuntimeExperienceCompositionContractTest {

    @Test
    fun composition_owns_experience_components() {
        val composition = DefaultRuntimeExperienceComposition()

        val engine = composition.experienceEngine()
        val decisionEngine = composition.experienceDecisionEngine()
        val store = composition.experienceStore()
        val consolidator = composition.experienceConsolidator()

        assertSame(engine, composition.experienceEngine())
        assertSame(decisionEngine, composition.experienceDecisionEngine())
        assertSame(store, composition.experienceStore())
        assertSame(consolidator, composition.experienceConsolidator())
    }

    @Test
    fun separate_compositions_do_not_share_experience_components() {
        val first = DefaultRuntimeExperienceComposition()
        val second = DefaultRuntimeExperienceComposition()

        assertNotSame(
            first.experienceEngine(),
            second.experienceEngine()
        )

        assertNotSame(
            first.experienceDecisionEngine(),
            second.experienceDecisionEngine()
        )

        assertNotSame(
            first.experienceStore(),
            second.experienceStore()
        )

        assertNotSame(
            first.experienceConsolidator(),
            second.experienceConsolidator()
        )
    }
    @Test
    fun composition_preserves_experience_store_state() {
        val composition = DefaultRuntimeExperienceComposition()
        val store = composition.experienceStore()

        val experience = RuntimeExperience(
            description = "stored runtime experience",
            meaning = RuntimeMeaningResult(
                interpretation = "runtime meaning",
                confidence = 1.0,
                significance = RuntimeMeaningSignificance.WARNING,
                generatedAt = 1L
            ),
            importance = RuntimeExperienceImportance.MEDIUM,
            createdAt = 1L
        )

        store.append(experience)

        assertSame(store, composition.experienceStore())
        assertEquals(
            listOf(experience),
            composition.experienceStore().experiences()
        )
    }

    @Test
    fun separate_compositions_have_independent_experience_store_state() {
        val first = DefaultRuntimeExperienceComposition()
        val second = DefaultRuntimeExperienceComposition()

        val experience = RuntimeExperience(
            description = "isolated runtime experience",
            meaning = RuntimeMeaningResult(
                interpretation = "isolated meaning",
                confidence = 1.0,
                significance = RuntimeMeaningSignificance.WARNING,
                generatedAt = 1L
            ),
            importance = RuntimeExperienceImportance.MEDIUM,
            createdAt = 1L
        )

        first.experienceStore().append(experience)

        assertEquals(
            listOf(experience),
            first.experienceStore().experiences()
        )

        assertEquals(
            emptyList(),
            second.experienceStore().experiences()
        )
    }

}
