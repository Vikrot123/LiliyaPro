package pro.liliya.core.runtime.intelligence.knowledge.quality

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

interface RuntimeKnowledgeQualityEvaluator {

    fun evaluate(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeQualityAssessment
}
