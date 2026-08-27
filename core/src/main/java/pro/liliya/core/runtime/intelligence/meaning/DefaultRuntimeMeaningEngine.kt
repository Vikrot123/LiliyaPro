package pro.liliya.core.runtime.intelligence.meaning

import pro.liliya.core.runtime.intelligence.context.cognitive.source.KnowledgeCognitiveContextSource
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.reflection.trend.RuntimeReflectionStability

class DefaultRuntimeMeaningEngine : RuntimeMeaningEngine {

    override fun interpret(
        context: RuntimeMeaningContext
    ): RuntimeMeaningResult {

        val significance: RuntimeMeaningSignificance
        val interpretation: String
        val confidence: Double

        when {
            context.reflection.healthy &&
                context.trend.stability == RuntimeReflectionStability.STABLE -> {

                significance = RuntimeMeaningSignificance.STABLE
                interpretation =
                    "Runtime maintains stable operational state"
                confidence = 0.95
            }

            context.trend.stability == RuntimeReflectionStability.DEGRADED -> {

                significance = RuntimeMeaningSignificance.WARNING
                interpretation =
                    "Runtime shows signs of degradation"
                confidence = 0.75
            }

            context.trend.stability == RuntimeReflectionStability.UNSTABLE -> {

                significance = RuntimeMeaningSignificance.CRITICAL
                interpretation =
                    "Runtime instability requires attention"
                confidence = 0.85
            }

            else -> {

                significance = RuntimeMeaningSignificance.UNKNOWN
                interpretation =
                    "Runtime state requires further observation"
                confidence = 0.5
            }
        }

        val availableKnowledge =
            context.cognitiveContext
                ?.values
                ?.get(
                    KnowledgeCognitiveContextSource
                        .AVAILABLE_KNOWLEDGE_KEY
                ) as? List<*>

        val typedKnowledge =
            availableKnowledge
                ?.filterIsInstance<RuntimeKnowledge>()

        val relevantKnowledge =
            typedKnowledge
                ?.filter { knowledge ->
                    isKnowledgeRelevant(
                        statement = knowledge.statement,
                        interpretation = interpretation
                    )
                }

        val selectionPool =
            if (relevantKnowledge.isNullOrEmpty()) {
                typedKnowledge
            } else {
                relevantKnowledge
            }

        val selectedKnowledge =
            selectionPool
                ?.maxWithOrNull(
                    compareBy<RuntimeKnowledge> {
                        it.confidence
                    }.thenBy {
                        it.createdAt
                    }
                )

        val enrichedInterpretation =
            if (selectedKnowledge == null) {
                interpretation
            } else {
                "$interpretation; available knowledge: ${selectedKnowledge.statement}"
            }

        return RuntimeMeaningResult(
            interpretation = enrichedInterpretation,
            confidence = confidence,
            significance = significance,
            generatedAt = System.currentTimeMillis()
        )
    }

    private fun isKnowledgeRelevant(
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
            semanticTokens(interpretation)

        val statementTokens =
            semanticTokens(statement)

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

    private fun semanticTokens(
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
