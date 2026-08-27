package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.provenance

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

interface RuntimeKnowledgeProvenanceQuery {

    fun provenance(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeProvenance
}
