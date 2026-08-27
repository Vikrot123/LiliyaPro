package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.decision.quality.RuntimeDecisionQualityAssessment
import pro.liliya.core.runtime.intelligence.decision.quality.RuntimeDecisionQualityState
import pro.liliya.core.runtime.intelligence.decision.quality.history.DefaultRuntimeDecisionQualityHistory
import pro.liliya.core.runtime.intelligence.decision.reflection.RuntimeDecisionReflectionEvidence
import pro.liliya.core.runtime.intelligence.decision.reflection.RuntimeDecisionReflectionInsight
import pro.liliya.core.runtime.intelligence.decision.reflection.trend.RuntimeDecisionReflectionTrend
import pro.liliya.core.runtime.intelligence.decision.reflection.trend.RuntimeDecisionReflectionTrendState

class DefaultRuntimeDecisionQualityHistoryContractTest {

    @Test
    fun history_is_bounded_and_preserves_newest_assessments() {
        var now = 0L

        val history =
            DefaultRuntimeDecisionQualityHistory(
                capacity = 2,
                clock = {
                    now += 1L
                    now
                }
            )

        val first =
            assessment(
                RuntimeDecisionQualityState
                    .INSUFFICIENT_DATA
            )

        val second =
            assessment(
                RuntimeDecisionQualityState.TRUSTWORTHY
            )

        val third =
            assessment(
                RuntimeDecisionQualityState.UNSTABLE
            )

        history.record(first)
        history.record(second)
        history.record(third)

        val records =
            history.records()

        assertEquals(
            2,
            records.size
        )

        assertEquals(
            listOf(
                second,
                third
            ),
            records.map {
                it.assessment
            }
        )

        assertEquals(
            listOf(
                2L,
                3L
            ),
            records.map {
                it.recordedAt
            }
        )
    }

    @Test
    fun clear_removes_all_quality_records() {
        val history =
            DefaultRuntimeDecisionQualityHistory()

        history.record(
            assessment(
                RuntimeDecisionQualityState.TRUSTWORTHY
            )
        )

        history.clear()

        assertEquals(
            emptyList(),
            history.records()
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
                                "quality history",
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
                        "quality history"
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
            reason = "quality history"
        )
}
