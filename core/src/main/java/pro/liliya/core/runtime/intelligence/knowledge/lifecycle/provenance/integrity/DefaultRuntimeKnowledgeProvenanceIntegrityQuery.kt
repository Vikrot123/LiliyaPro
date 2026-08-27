package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.provenance.integrity

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.provenance.RuntimeKnowledgeProvenanceQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.supersession.integrity.RuntimeKnowledgeSupersessionIntegrityChecker
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.supersession.integrity.RuntimeKnowledgeSupersessionIntegrityIssue

class DefaultRuntimeKnowledgeProvenanceIntegrityQuery(
    private val provenanceQuery:
        RuntimeKnowledgeProvenanceQuery,
    private val integrityChecker:
        RuntimeKnowledgeSupersessionIntegrityChecker
) : RuntimeKnowledgeProvenanceIntegrityQuery {

    override fun check(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeProvenanceIntegrity {

        val provenance =
            provenanceQuery.provenanceForCurrent(
                knowledge
            )

        val chain =
            provenance.chain.toSet()

        val issues =
            integrityChecker
                .check()
                .issues
                .filter { issue ->
                    belongsToChain(
                        issue = issue,
                        chain = chain
                    )
                }

        return RuntimeKnowledgeProvenanceIntegrity(
            issues = issues
        )
    }

    private fun belongsToChain(
        issue:
            RuntimeKnowledgeSupersessionIntegrityIssue,
        chain: Set<RuntimeKnowledge>
    ): Boolean {

        return when (issue) {
            is RuntimeKnowledgeSupersessionIntegrityIssue.SelfSupersession ->
                issue.knowledge in chain

            is RuntimeKnowledgeSupersessionIntegrityIssue.DuplicateEdge ->
                issue.previousKnowledge in chain ||
                    issue.replacementKnowledge in chain

            is RuntimeKnowledgeSupersessionIntegrityIssue.Branching ->
                issue.previousKnowledge in chain ||
                    issue.replacements.any {
                        it in chain
                    }

            is RuntimeKnowledgeSupersessionIntegrityIssue.Cycle ->
                issue.chain.any {
                    it in chain
                }
        }
    }
}
