package pro.liliya.core.runtime.intelligence.decision.quality

import pro.liliya.core.runtime.intelligence.decision.reflection.RuntimeDecisionReflectionInsight
import pro.liliya.core.runtime.intelligence.decision.reflection.trend.RuntimeDecisionReflectionTrend
import pro.liliya.core.runtime.intelligence.decision.reflection.trend.RuntimeDecisionReflectionTrendState

class DefaultRuntimeDecisionQualityAssessor :
    RuntimeDecisionQualityAssessor {

    override fun assess(
        reflection: RuntimeDecisionReflectionInsight,
        trend: RuntimeDecisionReflectionTrend
    ): RuntimeDecisionQualityAssessment {

        val temporallyStable =
            !trend.oscillating

        val state =
            when {
                reflection.requiresAttention ->
                    RuntimeDecisionQualityState
                        .REQUIRES_ATTENTION

                trend.state ==
                    RuntimeDecisionReflectionTrendState.DEGRADING ->
                    RuntimeDecisionQualityState
                        .REQUIRES_ATTENTION

                trend.oscillating ->
                    RuntimeDecisionQualityState.UNSTABLE

                trend.state ==
                    RuntimeDecisionReflectionTrendState
                        .INSUFFICIENT_DATA ->
                    RuntimeDecisionQualityState
                        .INSUFFICIENT_DATA

                reflection.trustworthyKnowledgeBasis ->
                    RuntimeDecisionQualityState.TRUSTWORTHY

                else ->
                    RuntimeDecisionQualityState
                        .REQUIRES_ATTENTION
            }

        val reason =
            when (state) {
                RuntimeDecisionQualityState.REQUIRES_ATTENTION ->
                    if (reflection.requiresAttention) {
                        "Current decision reflection requires attention"
                    } else {
                        "Decision reflection trend is degrading"
                    }

                RuntimeDecisionQualityState.UNSTABLE ->
                    "Decision reflection quality is temporally unstable"

                RuntimeDecisionQualityState.INSUFFICIENT_DATA ->
                    "Decision quality history is insufficient"

                RuntimeDecisionQualityState.TRUSTWORTHY ->
                    "Decision uses trustworthy knowledge with stable reflection quality"
            }

        return RuntimeDecisionQualityAssessment(
            state = state,
            reflection = reflection,
            trend = trend,
            trustworthyKnowledgeBasis =
                reflection.trustworthyKnowledgeBasis,
            requiresAttention =
                state ==
                    RuntimeDecisionQualityState
                        .REQUIRES_ATTENTION,
            temporallyStable =
                temporallyStable,
            confidence =
                reflection.evidence.confidence,
            reason =
                reason
        )
    }
}
