package pro.liliya.core.runtime.intelligence.decision.reflection

import pro.liliya.core.runtime.control.RuntimeCommand

data class RuntimeDecisionReflectionEvidence(
    val command: RuntimeCommand?,
    val decisionReason: String,
    val confidence: Double,
    val knowledgeUsed: Boolean,
    val provenanceAvailable: Boolean,
    val provenanceValid: Boolean?,
    val provenanceDepth: Int
)
