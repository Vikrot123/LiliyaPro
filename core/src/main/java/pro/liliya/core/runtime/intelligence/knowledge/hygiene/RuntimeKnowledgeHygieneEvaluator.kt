package pro.liliya.core.runtime.intelligence.knowledge.hygiene

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

interface RuntimeKnowledgeHygieneEvaluator {

    fun findEquivalent(
        knowledge: RuntimeKnowledge,
        existing: List<RuntimeKnowledge>
    ): RuntimeKnowledge?
}
