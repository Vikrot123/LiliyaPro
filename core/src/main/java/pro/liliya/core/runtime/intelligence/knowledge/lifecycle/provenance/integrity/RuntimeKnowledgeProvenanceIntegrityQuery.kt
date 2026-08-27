package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.provenance.integrity

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

interface RuntimeKnowledgeProvenanceIntegrityQuery {

    fun check(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeProvenanceIntegrity
}
