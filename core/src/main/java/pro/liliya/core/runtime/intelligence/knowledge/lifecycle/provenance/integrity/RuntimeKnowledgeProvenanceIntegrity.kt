package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.provenance.integrity

import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.supersession.integrity.RuntimeKnowledgeSupersessionIntegrityIssue

data class RuntimeKnowledgeProvenanceIntegrity(
    val issues:
        List<RuntimeKnowledgeSupersessionIntegrityIssue>
) {
    val valid: Boolean
        get() = issues.isEmpty()

    val issueCount: Int
        get() = issues.size
}
