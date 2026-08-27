package pro.liliya.core.runtime.intelligence.decision.explanation

import pro.liliya.core.runtime.intelligence.decision.RuntimeDecision
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.provenance.RuntimeKnowledgeProvenanceQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.provenance.integrity.RuntimeKnowledgeProvenanceIntegrityQuery
import pro.liliya.core.runtime.intelligence.decision.quality.governance.RuntimeDecisionQualityGovernanceQuery

class DefaultRuntimeDecisionExplainer(
    private val provenanceQuery:
        RuntimeKnowledgeProvenanceQuery? = null,
    private val provenanceIntegrityQuery:
        RuntimeKnowledgeProvenanceIntegrityQuery? = null,
    private val governanceQuery:
        RuntimeDecisionQualityGovernanceQuery? = null
) : RuntimeDecisionExplainer {

    override fun explain(
        decision: RuntimeDecision
    ): RuntimeDecisionExplanation {

        val selection =
            decision.knowledgeSelection

        val selectedKnowledge =
            selection?.knowledge

        val provenance =
            if (
                selectedKnowledge != null &&
                provenanceQuery != null
            ) {
                provenanceQuery.provenanceForCurrent(
                    selectedKnowledge
                )
            } else {
                null
            }

        val provenanceIntegrity =
            if (
                selectedKnowledge != null &&
                provenanceIntegrityQuery != null
            ) {
                provenanceIntegrityQuery.check(
                    selectedKnowledge
                )
            } else {
                null
            }

        val governanceAssessment =
            governanceQuery?.currentAssessment()

        return RuntimeDecisionExplanation(
            command = decision.command,
            decisionReason = decision.reason,
            confidence = decision.confidence,
            knowledgeStatement =
                selectedKnowledge?.statement,
            knowledgeSelectionReason =
                selection?.selectionReason,
            knowledgeRelevanceScore =
                selection?.relevanceScore ?: 0.0,
            knowledgeProvenance =
                provenance,
            knowledgeProvenanceIntegrity =
                provenanceIntegrity,
            decisionQualityGovernanceAssessment =
                governanceAssessment
        )
    }
}
