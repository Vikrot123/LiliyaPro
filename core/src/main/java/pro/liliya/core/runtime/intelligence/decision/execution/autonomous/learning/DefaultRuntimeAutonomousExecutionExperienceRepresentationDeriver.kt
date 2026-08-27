package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning

import pro.liliya.core.runtime.intelligence.experience.RuntimeExperience
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceImportance
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class DefaultRuntimeAutonomousExecutionExperienceRepresentationDeriver :
    RuntimeAutonomousExecutionExperienceRepresentationDeriver {

    override fun derive(
        materialization:
            RuntimeAutonomousExecutionExperienceMaterialization
    ): RuntimeAutonomousExecutionExperienceRepresentation {

        val intelligence =
            materialization
                .analysis
                .evidence
                .evaluation
                .executionResult
                .intelligence

        val meaning =
            intelligence.meaning

        val importance =
            when (meaning.significance) {
                RuntimeMeaningSignificance.STABLE ->
                    RuntimeExperienceImportance.LOW

                RuntimeMeaningSignificance.WARNING ->
                    RuntimeExperienceImportance.MEDIUM

                RuntimeMeaningSignificance.CRITICAL ->
                    RuntimeExperienceImportance.HIGH

                RuntimeMeaningSignificance.UNKNOWN ->
                    RuntimeExperienceImportance.NONE
            }

        val experience =
            RuntimeExperience(
                description =
                    materialization.description,
                meaning =
                    meaning,
                importance =
                    importance,
                createdAt =
                    System.currentTimeMillis()
            )

        return RuntimeAutonomousExecutionExperienceRepresentation(
            materialization =
                materialization,
            experience =
                experience
        )
    }
}
