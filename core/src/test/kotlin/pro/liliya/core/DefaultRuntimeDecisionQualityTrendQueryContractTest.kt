package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.decision.quality.RuntimeDecisionQualityAssessment
import pro.liliya.core.runtime.intelligence.decision.quality.RuntimeDecisionQualityState
import pro.liliya.core.runtime.intelligence.decision.quality.history.DefaultRuntimeDecisionQualityHistory
import pro.liliya.core.runtime.intelligence.decision.quality.trend.DefaultRuntimeDecisionQualityTrendQuery
import pro.liliya.core.runtime.intelligence.decision.quality.trend.RuntimeDecisionQualityTrendState
import pro.liliya.core.runtime.intelligence.decision.reflection.RuntimeDecisionReflectionEvidence
import pro.liliya.core.runtime.intelligence.decision.reflection.RuntimeDecisionReflectionInsight
import pro.liliya.core.runtime.intelligence.decision.reflection.trend.RuntimeDecisionReflectionTrend
import pro.liliya.core.runtime.intelligence.decision.reflection.trend.RuntimeDecisionReflectionTrendState

class DefaultRuntimeDecisionQualityTrendQueryContractTest {

    @Test
    fun query_reads_current_bounded_quality_history_snapshot() {
        val history =
            DefaultRuntimeDecisionQualityHistory(
                capacity = 2
            )

        val query =
            DefaultRuntimeDecisionQualityTrendQuery(
                history = history
            )

        history.record(
            assessment(
                RuntimeDecisionQualityState
                    .REQUIRES_ATTENTION
            )
        )

        history.record(
            assessment(
                RuntimeDecisionQualityState.UNSTABLE
            )
        )

        history.record(
            assessment(
                RuntimeDecisionQualityState.TRUSTWORTHY
            )
        )

        val trend =
            query.currentTrend()

        assertEquals(
            2,
            trend.sampleCount
        )

        assertEquals(
            RuntimeDecisionQualityState.UNSTABLE,
            trend.firstQualityState
        )

        assertEquals(
            RuntimeDecisionQualityState.TRUSTWORTHY,
            trend.latestQualityState
        )

        assertEquals(
            RuntimeDecisionQualityTrendState.IMPROVING,
            trend.state
        )
    }

    @Test
    fun clear_immediately_returns_insufficient_quality_trend() {
        val history =
            DefaultRuntimeDecisionQualityHistory()

        val query =
            DefaultRuntimeDecisionQualityTrendQuery(
                history = history
            )

        history.record(
            assessment(
                RuntimeDecisionQualityState.TRUSTWORTHY
            )
        )

        history.record(
            assessment(
                RuntimeDecisionQualityState.TRUSTWORTHY
            )
        )

        history.clear()

        val trend =
            query.currentTrend()

        assertEquals(
            RuntimeDecisionQualityTrendState
                .INSUFFICIENT_DATA,
            trend.state
        )

        assertEquals(
            0,
            trend.sampleCount
        )
    }

    private fun assessment(
        state: RuntimeDecisionQualityState
    ) =
        RuntimeDecisionQualityAssessment(
            state = state,
            reflection =
                RuntimeDecisionReflectionInsight(
                    evidence =
                        RuntimeDecisionReflectionEvidence(
                            command = null,
                            decisionReason =
                                "quality query",
                            confidence = 0.90,
                            knowledgeUsed = false,
                            provenanceAvailable = false,
                            provenanceValid = null,
                            provenanceDepth = 0
                        ),
                    trustworthyKnowledgeBasis =
                        state ==
                            RuntimeDecisionQualityState
                                .TRUSTWORTHY,
                    requiresAttention =
                        state ==
                            RuntimeDecisionQualityState
                                .REQUIRES_ATTENTION,
                    summary =
                        "quality query"
                ),
            trend =
                RuntimeDecisionReflectionTrend(
                    state =
                        RuntimeDecisionReflectionTrendState.STABLE,
                    sampleCount = 2,
                    trustworthyRatio = 1.0,
                    attentionRatio = 0.0,
                    averageConfidence = 0.90,
                    firstConfidence = 0.90,
                    latestConfidence = 0.90,
                    confidenceDelta = 0.0
                ),
            trustworthyKnowledgeBasis =
                state ==
                    RuntimeDecisionQualityState.TRUSTWORTHY,
            requiresAttention =
                state ==
                    RuntimeDecisionQualityState
                        .REQUIRES_ATTENTION,
            temporallyStable = true,
            confidence = 0.90,
            reason =
                "quality query"
        )
}
