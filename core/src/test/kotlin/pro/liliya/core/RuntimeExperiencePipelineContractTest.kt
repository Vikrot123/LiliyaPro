package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals

import pro.liliya.core.runtime.intelligence.experience.RuntimeExperience
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceImportance
import pro.liliya.core.runtime.intelligence.experience.decision.RuntimeExperienceDecision
import pro.liliya.core.runtime.intelligence.experience.pipeline.RuntimeExperiencePipelineResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeExperiencePipelineContractTest {

    private fun experience(): RuntimeExperience {
        return RuntimeExperience(
            description = "pipeline experience",
            meaning = RuntimeMeaningResult(
                interpretation = "pipeline meaning",
                confidence = 1.0,
                significance = RuntimeMeaningSignificance.WARNING,
                generatedAt = 1L
            ),
            importance = RuntimeExperienceImportance.MEDIUM,
            createdAt = 1L
        )
    }

    @Test
    fun result_preserves_experience_and_decision() {
        val experience = experience()
        val decision = RuntimeExperienceDecision(
            shouldRemember = true,
            reason = "Experience may be useful later"
        )

        val result = RuntimeExperiencePipelineResult(
            experience = experience,
            decision = decision
        )

        assertEquals(experience, result.experience)
        assertEquals(decision, result.decision)
    }

    @Test
    fun result_preserves_rejection_decision() {
        val experience = experience()
        val decision = RuntimeExperienceDecision(
            shouldRemember = false,
            reason = "Experience has low importance"
        )

        val result = RuntimeExperiencePipelineResult(
            experience = experience,
            decision = decision
        )

        assertEquals(experience, result.experience)
        assertEquals(false, result.decision.shouldRemember)
        assertEquals(
            "Experience has low importance",
            result.decision.reason
        )
    }
}
