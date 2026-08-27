package pro.liliya.core.runtime.intelligence.decision.quality

import pro.liliya.core.runtime.intelligence.decision.reflection.RuntimeDecisionReflectionInsight
import pro.liliya.core.runtime.intelligence.decision.reflection.trend.RuntimeDecisionReflectionTrend

interface RuntimeDecisionQualityAssessor {

    fun assess(
        reflection: RuntimeDecisionReflectionInsight,
        trend: RuntimeDecisionReflectionTrend
    ): RuntimeDecisionQualityAssessment
}
