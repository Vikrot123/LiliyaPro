package pro.liliya.core.runtime.intelligence.decision.explanation

import pro.liliya.core.runtime.intelligence.decision.RuntimeDecision
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.provenance.RuntimeKnowledgeProvenanceQuery

class DefaultRuntimeDecisionExplainer(
    private val provenanceQuery:
        RuntimeKnowledgeProvenanceQuery? = null
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
                provenance
        )
    }
}
