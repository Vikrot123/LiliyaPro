package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperience
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceImportance
import pro.liliya.core.runtime.intelligence.experience.store.DefaultRuntimeExperienceStore
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class DefaultRuntimeExperienceStoreContractTest {

    private fun experience(
        text: String
    ): RuntimeExperience {
        return RuntimeExperience(
            description = text,
            meaning = RuntimeMeaningResult(
                interpretation = text,
                confidence = 1.0,
                significance = RuntimeMeaningSignificance.STABLE,
                generatedAt = 1L
            ),
            importance = RuntimeExperienceImportance.LOW,
            createdAt = 1L
        )
    }

    @Test
    fun store_keeps_experience_order() {

        val store = DefaultRuntimeExperienceStore()

        val first = experience("first")
        val second = experience("second")

        store.append(first)
        store.append(second)

        assertEquals(
            listOf(first, second),
            store.experiences()
        )
    }

    @Test
    fun store_does_not_expose_internal_storage() {

        val store = DefaultRuntimeExperienceStore()

        store.append(
            experience("state")
        )

        val first = store.experiences()
        val second = store.experiences()

        assertNotSame(
            first,
            second
        )
    }
}
