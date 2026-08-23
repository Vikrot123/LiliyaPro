package pro.liliya.core.runtime.intelligence.knowledge.association.store

import pro.liliya.core.runtime.intelligence.knowledge.association.RuntimeKnowledgeAssociation

interface RuntimeKnowledgeAssociationStore {

    fun append(
        association: RuntimeKnowledgeAssociation
    )

    fun associations(): List<RuntimeKnowledgeAssociation>
}
