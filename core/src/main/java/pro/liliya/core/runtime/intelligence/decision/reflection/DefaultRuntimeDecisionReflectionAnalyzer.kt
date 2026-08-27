package pro.liliya.core.runtime.intelligence.decision.reflection

import pro.liliya.core.runtime.intelligence.decision.explanation.RuntimeDecisionExplanation

class DefaultRuntimeDecisionReflectionAnalyzer :
    RuntimeDecisionReflectionAnalyzer {

    override fun analyze(
        explanation: RuntimeDecisionExplanation
    ): RuntimeDecisionReflectionInsight {

        val provenance =
            explanation.knowledgeProvenance

        val integrity =
            explanation.knowledgeProvenanceIntegrity

        val knowledgeUsed =
            explanation.knowledgeStatement != null

        val provenanceAvailable =
            provenance != null

        val provenanceValid =
            integrity?.valid

        val provenanceDepth =
            provenance?.chain?.size ?: 0

        val trustworthyKnowledgeBasis =
            knowledgeUsed &&
                provenanceAvailable &&
                provenanceValid == true

        val requiresAttention =
            knowledgeUsed &&
                (
                    !provenanceAvailable ||
                        provenanceValid != true
                )

        val summary =
            when {
                !knowledgeUsed ->
                    "Decision was made without selected knowledge"

                trustworthyKnowledgeBasis ->
                    "Decision used knowledge with valid provenance"

                !provenanceAvailable ->
                    "Decision used knowledge without observable provenance"

                provenanceValid == false ->
                    "Decision used knowledge with invalid provenance"

                else ->
                    "Decision knowledge provenance requires attention"
            }

        return RuntimeDecisionReflectionInsight(
            evidence =
                RuntimeDecisionReflectionEvidence(
                    command = explanation.command,
                    decisionReason =
                        explanation.decisionReason,
                    confidence =
                        explanation.confidence,
                    knowledgeUsed =
                        knowledgeUsed,
                    provenanceAvailable =
                        provenanceAvailable,
                    provenanceValid =
                        provenanceValid,
                    provenanceDepth =
                        provenanceDepth
                ),
            trustworthyKnowledgeBasis =
                trustworthyKnowledgeBasis,
            requiresAttention =
                requiresAttention,
            summary =
                summary
        )
    }
}
