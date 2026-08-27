package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection

import pro.liliya.core.runtime.intelligence.decision.explanation.RuntimeDecisionExplanation

class DefaultRuntimeAutonomousExecutionReflectionExplanationDeriver :
    RuntimeAutonomousExecutionReflectionExplanationDeriver {

    override fun derive(
        evidence: RuntimeAutonomousExecutionReflectionEvidence
    ): RuntimeDecisionExplanation {

        if (!evidence.consistent) {
            return RuntimeDecisionExplanation(
                command = null,
                decisionReason =
                    "Autonomous execution reflection evidence is inconsistent",
                confidence = 0.0,
                knowledgeStatement = null,
                knowledgeSelectionReason = null,
                knowledgeRelevanceScore = 0.0,
                knowledgeProvenance = null,
                knowledgeProvenanceIntegrity = null,
                decisionQualityGovernanceAssessment = null
            )
        }

        return RuntimeDecisionExplanation(
            command = evidence.command,
            decisionReason = evidence.decisionReason,
            confidence = evidence.confidence,
            knowledgeStatement = null,
            knowledgeSelectionReason = null,
            knowledgeRelevanceScore = 0.0,
            knowledgeProvenance = null,
            knowledgeProvenanceIntegrity = null,
            decisionQualityGovernanceAssessment = null
        )
    }
}
