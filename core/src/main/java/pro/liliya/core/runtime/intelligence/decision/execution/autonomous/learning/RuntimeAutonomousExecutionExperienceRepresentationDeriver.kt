package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning

interface RuntimeAutonomousExecutionExperienceRepresentationDeriver {

    fun derive(
        materialization:
            RuntimeAutonomousExecutionExperienceMaterialization
    ): RuntimeAutonomousExecutionExperienceRepresentation
}
