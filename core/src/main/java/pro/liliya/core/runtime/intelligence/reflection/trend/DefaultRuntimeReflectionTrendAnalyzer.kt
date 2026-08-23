package pro.liliya.core.runtime.intelligence.reflection.trend

import pro.liliya.core.runtime.intelligence.reflection.RuntimeReflectionSnapshot

class DefaultRuntimeReflectionTrendAnalyzer :
    RuntimeReflectionTrendAnalyzer {

    override fun analyze(
        history: List<RuntimeReflectionSnapshot>
    ): RuntimeReflectionTrend {

        if (history.isEmpty()) {
            return RuntimeReflectionTrend(
                stability = RuntimeReflectionStability.UNKNOWN,
                healthyRatio = 0.0,
                improving = false
            )
        }

        val healthyCount =
            history.count { it.healthy }

        val ratio =
            healthyCount.toDouble() / history.size.toDouble()

        val stability =
            when {
                ratio >= 0.8 -> RuntimeReflectionStability.STABLE
                ratio >= 0.5 -> RuntimeReflectionStability.DEGRADED
                else -> RuntimeReflectionStability.UNSTABLE
            }

        val improving =
            history.size >= 2 &&
            !history.last().healthy.not() &&
            history.last().healthy

        return RuntimeReflectionTrend(
            stability = stability,
            healthyRatio = ratio,
            improving = improving
        )
    }
}
