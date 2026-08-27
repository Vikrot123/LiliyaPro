package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.feedback

import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.assessment.RuntimeAutonomousExecutionAssessment

interface RuntimeAutonomousExecutionFeedbackDeriver {

    fun derive(
        assessment: RuntimeAutonomousExecutionAssessment
    ): RuntimeAutonomousExecutionFeedback
}
