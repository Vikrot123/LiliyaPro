package pro.liliya.core.runtime.intelligence.knowledge.relevance

class DefaultRuntimeKnowledgeRelevanceEvaluator :
    RuntimeKnowledgeRelevanceEvaluator {

    override fun evaluate(
        statement: String,
        interpretation: String
    ): RuntimeKnowledgeRelevance {

        if (
            statement.contains(
                interpretation,
                ignoreCase = true
            )
        ) {
            return RuntimeKnowledgeRelevance(
                relevant = true,
                score = 1.0
            )
        }

        val interpretationTokens =
            tokens(interpretation)

        if (interpretationTokens.isEmpty()) {
            return RuntimeKnowledgeRelevance(
                relevant = false,
                score = 0.0
            )
        }

        val shared =
            interpretationTokens.intersect(
                tokens(statement)
            )

        val score =
            shared.size.toDouble() /
                interpretationTokens.size.toDouble()

        return RuntimeKnowledgeRelevance(
            relevant =
                shared.size >= 2 &&
                    score >= 0.5,
            score = score
        )
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
