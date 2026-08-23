package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperience
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceImportance
import pro.liliya.core.runtime.intelligence.experience.decision.DefaultRuntimeExperienceDecisionEngine
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class DefaultRuntimeExperienceDecisionEngineContractTest {

    private fun experience(
        importance: RuntimeExperienceImportance
    ): RuntimeExperience {
        return RuntimeExperience(
            description = "test",
            meaning = RuntimeMeaningResult(
                interpretation = "test meaning",
                confidence = 1.0,
                significance = RuntimeMeaningSignificance.STABLE,
                generatedAt = 1L
            ),
            importance = importance,
            createdAt = 1L
        )
    }

    @Test
    fun critical_experience_should_be_remembered() {

        val result =
            DefaultRuntimeExperienceDecisionEngine()
                .decide(
                    experience(
                        RuntimeExperienceImportance.CRITICAL
                    )
                )

        assertTrue(result.shouldRemember)
    }

    @Test
    fun low_experience_should_not_be_remembered() {

        val result =
            DefaultRuntimeExperienceDecisionEngine()
                .decide(
                    experience(
                        RuntimeExperienceImportance.LOW
                    )
                )

        assertFalse(result.shouldRemember)
    }
}
