package pro.liliya.core.runtime.intelligence.knowledge.selection

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

class DefaultRuntimeKnowledgeSelector :
    RuntimeKnowledgeSelector {

    override fun select(
        knowledge: List<RuntimeKnowledge>,
        interpretation: String
    ): RuntimeKnowledge? {
        return selectResult(
            knowledge,
            interpretation
        ).knowledge
    }

    override fun selectResult(
        knowledge: List<RuntimeKnowledge>,
        interpretation: String
    ): RuntimeKnowledgeSelectionResult {

        val relevantKnowledge =
            knowledge.filter { item ->
                isRelevant(
                    statement = item.statement,
                    interpretation = interpretation
                )
            }

        val selectionPool =
            if (relevantKnowledge.isEmpty()) {
                knowledge
            } else {
                relevantKnowledge
            }

        val selected =
            selectionPool.maxWithOrNull(
                compareBy<RuntimeKnowledge> {
                    it.confidence
                }.thenBy {
                    it.createdAt
                }
            )

        return RuntimeKnowledgeSelectionResult(
            knowledge = selected,
            relevantPoolUsed = relevantKnowledge.isNotEmpty(),
            reason =
                if (relevantKnowledge.isNotEmpty()) {
                    "Selected from relevant knowledge pool"
                } else {
                    "Selected from fallback knowledge pool"
                }
        )
    }

    private fun isRelevant(
        statement: String,
        interpretation: String
    ): Boolean {

        if (
            statement.contains(
                interpretation,
                ignoreCase = true
            )
        ) {
            return true
        }

        val interpretationTokens =
            tokens(interpretation)

        val statementTokens =
            tokens(statement)

        if (interpretationTokens.isEmpty()) {
            return false
        }

        val shared =
            interpretationTokens.intersect(
                statementTokens
            )

        return shared.size >= 2 &&
            shared.size * 2 >= interpretationTokens.size
    }

    private fun tokens(
        text: String
    ): Set<String> {

        return text
            .lowercase()
            .split(
                Regex("[^a-z0-9]+")
            )
            .filter {
                it.length >= 3
            }
            .toSet()
    }
}
