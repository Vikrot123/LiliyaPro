package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.supersession.integrity

data class RuntimeKnowledgeSupersessionIntegrityReport(
    val issues:
        List<RuntimeKnowledgeSupersessionIntegrityIssue>
) {
    val valid: Boolean
        get() = issues.isEmpty()

    val issueCount: Int
        get() = issues.size
}
