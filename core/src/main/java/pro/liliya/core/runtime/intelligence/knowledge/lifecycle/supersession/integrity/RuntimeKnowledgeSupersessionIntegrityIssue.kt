package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.supersession.integrity

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

sealed class RuntimeKnowledgeSupersessionIntegrityIssue {

    data class SelfSupersession(
        val knowledge: RuntimeKnowledge
    ) : RuntimeKnowledgeSupersessionIntegrityIssue()

    data class DuplicateEdge(
        val previousKnowledge: RuntimeKnowledge,
        val replacementKnowledge: RuntimeKnowledge
    ) : RuntimeKnowledgeSupersessionIntegrityIssue()

    data class Branching(
        val previousKnowledge: RuntimeKnowledge,
        val replacements: List<RuntimeKnowledge>
    ) : RuntimeKnowledgeSupersessionIntegrityIssue()

    data class Cycle(
        val chain: List<RuntimeKnowledge>
    ) : RuntimeKnowledgeSupersessionIntegrityIssue()
}
