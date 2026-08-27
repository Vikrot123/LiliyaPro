package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning

import pro.liliya.core.runtime.intelligence.experience.RuntimeExperience

data class RuntimeAutonomousExecutionExperienceRepresentation(
    val materialization:
        RuntimeAutonomousExecutionExperienceMaterialization,
    val experience: RuntimeExperience
)
