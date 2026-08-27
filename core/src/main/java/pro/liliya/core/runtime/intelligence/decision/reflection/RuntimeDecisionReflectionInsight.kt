package pro.liliya.core.runtime.intelligence.decision.reflection

data class RuntimeDecisionReflectionInsight(
    val evidence: RuntimeDecisionReflectionEvidence,
    val trustworthyKnowledgeBasis: Boolean,
    val requiresAttention: Boolean,
    val summary: String
)
