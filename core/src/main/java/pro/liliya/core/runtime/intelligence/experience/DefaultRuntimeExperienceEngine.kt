package pro.liliya.core.runtime.intelligence.experience

import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class DefaultRuntimeExperienceEngine : RuntimeExperienceEngine {

    override fun createExperience(
        context: RuntimeExperienceContext
    ): RuntimeExperience {

        val importance = when (context.meaning.significance) {
            RuntimeMeaningSignificance.STABLE ->
                RuntimeExperienceImportance.LOW

            RuntimeMeaningSignificance.WARNING ->
                RuntimeExperienceImportance.MEDIUM

            RuntimeMeaningSignificance.CRITICAL ->
                RuntimeExperienceImportance.HIGH

            RuntimeMeaningSignificance.UNKNOWN ->
                RuntimeExperienceImportance.NONE
        }

        return RuntimeExperience(
            description = context.meaning.interpretation,
            meaning = context.meaning,
            importance = importance,
            createdAt = System.currentTimeMillis()
        )
    }
}
