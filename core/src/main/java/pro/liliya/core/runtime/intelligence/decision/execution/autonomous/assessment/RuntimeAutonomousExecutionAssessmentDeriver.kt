package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.assessment

import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.outcome.RuntimeAutonomousExecutionOutcome

interface RuntimeAutonomousExecutionAssessmentDeriver {

    fun derive(
        outcome: RuntimeAutonomousExecutionOutcome
    ): RuntimeAutonomousExecutionAssessment
}
