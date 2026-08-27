package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection

import pro.liliya.core.runtime.intelligence.decision.explanation.RuntimeDecisionExplanation

interface RuntimeAutonomousExecutionReflectionExplanationDeriver {

    fun derive(
        evidence: RuntimeAutonomousExecutionReflectionEvidence
    ): RuntimeDecisionExplanation
}
