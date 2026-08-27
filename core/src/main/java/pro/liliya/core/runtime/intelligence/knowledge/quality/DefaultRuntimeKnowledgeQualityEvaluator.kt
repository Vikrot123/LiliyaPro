package pro.liliya.core.runtime.intelligence.knowledge.quality

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.governance.DefaultRuntimeKnowledgeValidator
import pro.liliya.core.runtime.intelligence.knowledge.governance.RuntimeKnowledgeValidator
import pro.liliya.core.runtime.intelligence.knowledge.priority.DefaultRuntimeKnowledgePrioritizer
import pro.liliya.core.runtime.intelligence.knowledge.priority.RuntimeKnowledgePrioritizer

class DefaultRuntimeKnowledgeQualityEvaluator(
    private val validator: RuntimeKnowledgeValidator =
        DefaultRuntimeKnowledgeValidator(),
    private val prioritizer: RuntimeKnowledgePrioritizer =
        DefaultRuntimeKnowledgePrioritizer()
) : RuntimeKnowledgeQualityEvaluator {

    override fun evaluate(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeQualityAssessment {

        return RuntimeKnowledgeQualityAssessment(
            validation = validator.validate(knowledge),
            priority = prioritizer.prioritize(knowledge)
        )
    }
}
