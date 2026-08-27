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

        val latestKnowledge =
            availableKnowledge
                ?.filterIsInstance<RuntimeKnowledge>()
                ?.maxByOrNull { knowledge ->
                    knowledge.createdAt
                }

        val enrichedInterpretation =
            if (latestKnowledge == null) {
                interpretation
            } else {
                "$interpretation; available knowledge: ${latestKnowledge.statement}"
            }

        return RuntimeMeaningResult(
            interpretation = enrichedInterpretation,
            confidence = confidence,
            significance = significance,
            generatedAt = System.currentTimeMillis()
        )
    }
}
