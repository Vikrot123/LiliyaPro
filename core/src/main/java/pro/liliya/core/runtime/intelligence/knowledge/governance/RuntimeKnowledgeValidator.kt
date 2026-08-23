package pro.liliya.core.runtime.intelligence.knowledge.governance

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

interface RuntimeKnowledgeValidator {

    fun validate(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeValidation
}
