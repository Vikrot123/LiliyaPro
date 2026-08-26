package pro.liliya.core.runtime.intelligence.knowledge.association.store

import pro.liliya.core.runtime.intelligence.knowledge.association.RuntimeKnowledgeAssociation

class DefaultRuntimeKnowledgeAssociationStore :
    RuntimeKnowledgeAssociationStore {

    private val associations =
        mutableListOf<RuntimeKnowledgeAssociation>()

    override fun append(
        association: RuntimeKnowledgeAssociation
    ) {
        associations += association
    }

    override fun removeLast(
        association: RuntimeKnowledgeAssociation
    ) {
        if (associations.lastOrNull() != association) {
            return
        }

        associations.removeAt(
            associations.lastIndex
        )
    }

    override fun associations():
        List<RuntimeKnowledgeAssociation> {
        return associations.toList()
    }
}
