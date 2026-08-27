package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.RuntimeDecisionQualityAdvisory
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.RuntimeDecisionQualityAdvisoryLevel
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.summary.RuntimeDecisionQualityAdvisorySummary
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.summary.RuntimeDecisionQualityAdvisorySummaryState
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.trend.RuntimeDecisionQualityAdvisoryTrend
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.trend.RuntimeDecisionQualityAdvisoryTrendState
import pro.liliya.core.runtime.intelligence.decision.quality.governance.RuntimeDecisionQualityGovernanceAssessment
import pro.liliya.core.runtime.intelligence.decision.quality.governance.RuntimeDecisionQualityGovernanceState
import pro.liliya.core.runtime.intelligence.decision.quality.governance.history.DefaultRuntimeDecisionQualityGovernanceHistory
import pro.liliya.core.runtime.intelligence.decision.quality.summary.RuntimeDecisionQualitySummary
import pro.liliya.core.runtime.intelligence.decision.quality.summary.RuntimeDecisionQualitySummaryState
import pro.liliya.core.runtime.intelligence.decision.quality.trend.RuntimeDecisionQualityTrend
import pro.liliya.core.runtime.intelligence.decision.quality.trend.RuntimeDecisionQualityTrendState

class DefaultRuntimeDecisionQualityGovernanceHistoryContractTest {

    @Test
    fun history_is_bounded_and_preserves_newest_assessments() {
        var now = 0L

        val history =
            DefaultRuntimeDecisionQualityGovernanceHistory(
                capacity = 2,
                clock = {
                    now += 1L
                    now
                }
            )

        val first =
            assessment(
                RuntimeDecisionQualityGovernanceState
                    .INSUFFICIENT_DATA
            )

        val second =
            assessment(
                RuntimeDecisionQualityGovernanceState.CAUTION
            )

        val third =
            assessment(
                RuntimeDecisionQualityGovernanceState.REVIEW
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
    fun clear_removes_all_governance_records() {
        val history =
            DefaultRuntimeDecisionQualityGovernanceHistory()

        history.record(
            assessment(
                RuntimeDecisionQualityGovernanceState.CLEAR
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
            DefaultRuntimeDecisionQualityGovernanceHistory(
                capacity = 0
            )
        }
    }

    private fun assessment(
        state: RuntimeDecisionQualityGovernanceState
    ) =
        RuntimeDecisionQualityGovernanceAssessment(
            state = state,
            advisorySummary =
                RuntimeDecisionQualityAdvisorySummary(
                    state =
                        RuntimeDecisionQualityAdvisorySummaryState
                            .INSUFFICIENT_DATA,
                    advisory =
                        RuntimeDecisionQualityAdvisory(
                            level =
                                RuntimeDecisionQualityAdvisoryLevel
                                    .OBSERVE,
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
                                        "governance history fixture"
                                ),
                            actionRecommended = false,
                            humanReviewRecommended = false,
                            reason =
                                "governance history fixture"
                        ),
                    trend =
                        RuntimeDecisionQualityAdvisoryTrend(
                            state =
                                RuntimeDecisionQualityAdvisoryTrendState
                                    .INSUFFICIENT_DATA,
                            sampleCount = 0,
                            firstLevel = null,
                            latestLevel = null,
                            proceedRatio = 0.0,
                            cautionRatio = 0.0,
                            reviewRatio = 0.0,
                            advisoryTransitionCount = 0,
                            directionChanges = 0,
                            oscillating = false
                        ),
                    stable = false,
                    oscillating = false,
                    requiresReview =
                        state ==
                            RuntimeDecisionQualityGovernanceState
                                .REVIEW,
                    reason =
                        "governance history fixture"
                ),
            proceedWithCaution =
                state ==
                    RuntimeDecisionQualityGovernanceState
                        .CAUTION,
            reviewRecommended =
                state ==
                    RuntimeDecisionQualityGovernanceState
                        .REVIEW,
            reason =
                "governance history fixture"
        )
}
