package pro.liliya.core.runtime.intelligence.decision.reflection.trend

data class RuntimeDecisionReflectionTrend(
    val state: RuntimeDecisionReflectionTrendState,
    val sampleCount: Int,
    val trustworthyRatio: Double,
    val attentionRatio: Double,
    val averageConfidence: Double,
    val firstConfidence: Double?,
    val latestConfidence: Double?,
    val confidenceDelta: Double,
    val qualityTransitionCount: Int = 0,
    val confidenceDirectionChanges: Int = 0,
    val oscillating: Boolean = false
)
