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

        val selectionReason =
            when {
                selected == null ->
                    RuntimeKnowledgeSelectionReason.EMPTY

                relevantKnowledge.isNotEmpty() ->
                    RuntimeKnowledgeSelectionReason.RELEVANT_POOL

                else ->
                    RuntimeKnowledgeSelectionReason.FALLBACK_POOL
            }

        return RuntimeKnowledgeSelectionResult(
            knowledge = selected,
            relevantPoolUsed =
                selectionReason ==
                    RuntimeKnowledgeSelectionReason.RELEVANT_POOL,
            reason =
                when (selectionReason) {
                    RuntimeKnowledgeSelectionReason.RELEVANT_POOL ->
                        "Selected from relevant knowledge pool"

                    RuntimeKnowledgeSelectionReason.FALLBACK_POOL ->
                        "Selected from fallback knowledge pool"

                    RuntimeKnowledgeSelectionReason.EMPTY ->
                        "No knowledge available for selection"
                },
            selectionReason = selectionReason,
            relevanceScore =
                if (
                    selectionReason ==
                        RuntimeKnowledgeSelectionReason.RELEVANT_POOL &&
                    selected != null
                ) {
                    relevanceScore(
                        statement = selected.statement,
                        interpretation = interpretation
                    )
                } else {
                    0.0
                }
        )
    }

    private fun relevanceScore(
        statement: String,
        interpretation: String
    ): Double {

        if (
            statement.contains(
                interpretation,
                ignoreCase = true
            )
        ) {
            return 1.0
        }

        val interpretationTokens =
            tokens(interpretation)

        if (interpretationTokens.isEmpty()) {
            return 0.0
        }

        val shared =
            interpretationTokens.intersect(
                tokens(statement)
            )

        return shared.size.toDouble() /
            interpretationTokens.size.toDouble()
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
