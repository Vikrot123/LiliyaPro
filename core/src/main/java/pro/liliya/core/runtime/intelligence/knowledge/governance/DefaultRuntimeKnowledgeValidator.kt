package pro.liliya.core.runtime.intelligence.knowledge.governance

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

class DefaultRuntimeKnowledgeValidator :
    RuntimeKnowledgeValidator {

    override fun validate(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeValidation {

        return if (knowledge.confidence > 0.5) {
            RuntimeKnowledgeValidation(
                accepted = true,
                reason = "Knowledge confidence is sufficient",
                confidence = knowledge.confidence
            )
        } else {
            RuntimeKnowledgeValidation(
                accepted = false,
                reason = "Knowledge confidence is too low",
                confidence = knowledge.confidence
            )
        }
    }
}
