package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.RuntimeDecisionQualityAdvisory
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.RuntimeDecisionQualityAdvisoryLevel
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.history.DefaultRuntimeDecisionQualityAdvisoryHistory
import pro.liliya.core.runtime.intelligence.decision.quality.summary.RuntimeDecisionQualitySummary
import pro.liliya.core.runtime.intelligence.decision.quality.summary.RuntimeDecisionQualitySummaryState
import pro.liliya.core.runtime.intelligence.decision.quality.trend.RuntimeDecisionQualityTrend
import pro.liliya.core.runtime.intelligence.decision.quality.trend.RuntimeDecisionQualityTrendState

class DefaultRuntimeDecisionQualityAdvisoryHistoryContractTest {

    @Test
    fun history_is_bounded_and_preserves_newest_advisories() {
        var now = 0L

        val history =
            DefaultRuntimeDecisionQualityAdvisoryHistory(
                capacity = 2,
                clock = {
                    now += 1L
                    now
                }
            )

        val first =
            advisory(
                RuntimeDecisionQualityAdvisoryLevel.OBSERVE
            )

        val second =
            advisory(
                RuntimeDecisionQualityAdvisoryLevel.CAUTION
            )

        val third =
            advisory(
                RuntimeDecisionQualityAdvisoryLevel.REVIEW
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
                it.advisory
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
    fun clear_removes_all_advisory_records() {
        val history =
            DefaultRuntimeDecisionQualityAdvisoryHistory()

        history.record(
            advisory(
                RuntimeDecisionQualityAdvisoryLevel.PROCEED
            )
        )

        history.clear()

        assertEquals(
            emptyList(),
            history.records()
        )
    }

    @Test
    fun non_positive_capacity_is_rejected() {
        assertFailsWith<IllegalArgumentException> {
            DefaultRuntimeDecisionQualityAdvisoryHistory(
                capacity = 0
            )
        }
    }

    private fun advisory(
        level: RuntimeDecisionQualityAdvisoryLevel
    ) =
        RuntimeDecisionQualityAdvisory(
            level = level,
            summary =
                RuntimeDecisionQualitySummary(
                    state =
                        RuntimeDecisionQualitySummaryState
                            .INSUFFICIENT_DATA,
                    assessment = null,
                    trend =
                        RuntimeDecisionQualityTrend(
                            state =
                                RuntimeDecisionQualityTrendState
                                    .INSUFFICIENT_DATA,
                            sampleCount = 0,
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
                    temporallyStable = false,
                    oscillating = false,
                    reason =
                        "advisory history fixture"
                ),
            actionRecommended =
                level ==
                    RuntimeDecisionQualityAdvisoryLevel.PROCEED ||
                    level ==
                    RuntimeDecisionQualityAdvisoryLevel.CAUTION,
            humanReviewRecommended =
                level ==
                    RuntimeDecisionQualityAdvisoryLevel.REVIEW,
            reason =
                "advisory history fixture"
        )
}
