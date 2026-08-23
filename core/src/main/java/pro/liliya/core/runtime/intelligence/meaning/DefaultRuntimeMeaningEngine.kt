package pro.liliya.core.runtime.intelligence.meaning

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

        return RuntimeMeaningResult(
            interpretation = interpretation,
            confidence = confidence,
            significance = significance,
            generatedAt = System.currentTimeMillis()
        )
    }
}
