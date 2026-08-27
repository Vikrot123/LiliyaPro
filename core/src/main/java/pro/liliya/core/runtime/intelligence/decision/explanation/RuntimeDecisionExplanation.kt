package pro.liliya.core.runtime.intelligence.decision.explanation

import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionReason
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.provenance.RuntimeKnowledgeProvenance
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.provenance.integrity.RuntimeKnowledgeProvenanceIntegrity
import pro.liliya.core.runtime.intelligence.decision.quality.governance.RuntimeDecisionQualityGovernanceAssessment

data class RuntimeDecisionExplanation(
    val command: RuntimeCommand?,
    val decisionReason: String,
    val confidence: Double,
    val knowledgeStatement: String?,
    val knowledgeSelectionReason: RuntimeKnowledgeSelectionReason?,
    val knowledgeRelevanceScore: Double,
    val knowledgeProvenance: RuntimeKnowledgeProvenance? = null,
    val knowledgeProvenanceIntegrity:
        RuntimeKnowledgeProvenanceIntegrity? = null,
    val decisionQualityGovernanceAssessment:
        RuntimeDecisionQualityGovernanceAssessment? = null
)
