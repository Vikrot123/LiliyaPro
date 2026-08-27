package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.DefaultRuntimeDecisionQualityAdvisoryRecorder
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.RuntimeDecisionQualityAdvisory
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.RuntimeDecisionQualityAdvisoryLevel
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.RuntimeDecisionQualityAdvisoryQuery
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.history.DefaultRuntimeDecisionQualityAdvisoryHistory
import pro.liliya.core.runtime.intelligence.decision.quality.summary.RuntimeDecisionQualitySummary
import pro.liliya.core.runtime.intelligence.decision.quality.summary.RuntimeDecisionQualitySummaryState
import pro.liliya.core.runtime.intelligence.decision.quality.trend.RuntimeDecisionQualityTrend
import pro.liliya.core.runtime.intelligence.decision.quality.trend.RuntimeDecisionQualityTrendState

class DefaultRuntimeDecisionQualityAdvisoryRecorderContractTest {

    @Test
    fun record_current_snapshots_query_result_exactly_once() {
        val advisory =
            advisory(
                RuntimeDecisionQualityAdvisoryLevel.CAUTION
            )

        var calls = 0

        val query =
            object : RuntimeDecisionQualityAdvisoryQuery {
                override fun currentAdvisory():
                    RuntimeDecisionQualityAdvisory {

                    calls += 1

                    return advisory
                }
            }

        val history =
            DefaultRuntimeDecisionQualityAdvisoryHistory()

        val recorder =
            DefaultRuntimeDecisionQualityAdvisoryRecorder(
                query = query,
                history = history
            )

        val record =
            recorder.recordCurrent()

        assertEquals(
            1,
            calls
        )

        assertSame(
            advisory,
            record.advisory
        )

        assertEquals(
            listOf(record),
            history.records()
        )
    }

    private fun advisory(
        level: RuntimeDecisionQualityAdvisoryLevel
    ) =
        RuntimeDecisionQualityAdvisory(
            level = level,
            summary =
                RuntimeDecisionQualitySummary(
                    state =
                        RuntimeDecisionQualitySummaryState.WATCH,
                    assessment = null,
                    trend =
                        RuntimeDecisionQualityTrend(
                            state =
                                RuntimeDecisionQualityTrendState.STABLE,
                            sampleCount = 2,
                            firstQualityState = null,
                            latestQualityState = null,
                            trustworthyRatio = 0.0,
                            unstableRatio = 0.0,
                            attentionRatio = 0.0,
                            qualityTransitionCount = 0,
                            directionChanges = 0,
                            oscillating = false
                        ),
                    trustworthy = false,
                    requiresAttention = false,
                    temporallyStable = true,
                    oscillating = false,
                    reason =
                        "recorder fixture"
                ),
            actionRecommended = true,
            humanReviewRecommended = false,
            reason =
                "recorder fixture"
        )
}
