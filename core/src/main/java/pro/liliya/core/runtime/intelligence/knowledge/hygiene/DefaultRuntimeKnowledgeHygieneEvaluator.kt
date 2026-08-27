package pro.liliya.core.runtime.intelligence.knowledge.hygiene

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

class DefaultRuntimeKnowledgeHygieneEvaluator :
    RuntimeKnowledgeHygieneEvaluator {

    override fun findEquivalent(
        knowledge: RuntimeKnowledge,
        existing: List<RuntimeKnowledge>
    ): RuntimeKnowledge? {

        val normalized =
            normalize(knowledge.statement)

        return existing.firstOrNull {
            normalize(it.statement) == normalized
        }
    }

    private fun normalize(
        text: String
    ): String {

        return text
            .lowercase()
            .replace(
                Regex("[^a-z0-9]+"),
                " "
            )
            .trim()
            .replace(
                Regex("\\s+"),
                " "
            )
    }
}
