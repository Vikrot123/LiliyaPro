package pro.liliya.core.runtime.intelligence.decision.explanation

import pro.liliya.core.runtime.intelligence.decision.RuntimeDecision

class DefaultRuntimeDecisionExplainer :
    RuntimeDecisionExplainer {

    override fun explain(
        decision: RuntimeDecision
    ): RuntimeDecisionExplanation {

        val selection =
            decision.knowledgeSelection

        return RuntimeDecisionExplanation(
            command = decision.command,
            decisionReason = decision.reason,
            confidence = decision.confidence,
            knowledgeStatement =
                selection
                    ?.knowledge
                    ?.statement,
            knowledgeSelectionReason =
                selection?.selectionReason,
            knowledgeRelevanceScore =
                selection?.relevanceScore ?: 0.0
        )
    }
}
