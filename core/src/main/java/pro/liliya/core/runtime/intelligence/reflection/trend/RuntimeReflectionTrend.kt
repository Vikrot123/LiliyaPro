package pro.liliya.core.runtime.intelligence.reflection.trend

data class RuntimeReflectionTrend(
    val stability: RuntimeReflectionStability,
    val healthyRatio: Double,
    val improving: Boolean
)
