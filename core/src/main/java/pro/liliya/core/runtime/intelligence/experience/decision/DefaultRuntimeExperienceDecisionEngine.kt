package pro.liliya.core.runtime.intelligence.experience.decision

import pro.liliya.core.runtime.intelligence.experience.RuntimeExperience
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceImportance

class DefaultRuntimeExperienceDecisionEngine :
    RuntimeExperienceDecisionEngine {

    override fun decide(
        experience: RuntimeExperience
    ): RuntimeExperienceDecision {

        return when (experience.importance) {

            RuntimeExperienceImportance.NONE ->
                RuntimeExperienceDecision(
                    shouldRemember = false,
                    reason = "Experience has no significance"
                )

            RuntimeExperienceImportance.LOW ->
                RuntimeExperienceDecision(
                    shouldRemember = false,
                    reason = "Experience has low importance"
                )

            RuntimeExperienceImportance.MEDIUM ->
                RuntimeExperienceDecision(
                    shouldRemember = true,
                    reason = "Experience may be useful later"
                )

            RuntimeExperienceImportance.HIGH ->
                RuntimeExperienceDecision(
                    shouldRemember = true,
                    reason = "Experience has operational impact"
                )

            RuntimeExperienceImportance.CRITICAL ->
                RuntimeExperienceDecision(
                    shouldRemember = true,
                    reason = "Experience requires preservation"
                )
        }
    }
}
