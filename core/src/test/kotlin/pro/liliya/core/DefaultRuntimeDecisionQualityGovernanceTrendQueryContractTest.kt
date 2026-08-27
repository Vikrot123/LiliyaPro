package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.RuntimeDecisionQualityAdvisory
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.RuntimeDecisionQualityAdvisoryLevel
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.summary.RuntimeDecisionQualityAdvisorySummary
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.summary.RuntimeDecisionQualityAdvisorySummaryState
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.trend.RuntimeDecisionQualityAdvisoryTrend
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.trend.RuntimeDecisionQualityAdvisoryTrendState
import pro.liliya.core.runtime.intelligence.decision.quality.governance.RuntimeDecisionQualityGovernanceAssessment
import pro.liliya.core.runtime.intelligence.decision.quality.governance.RuntimeDecisionQualityGovernanceState
import pro.liliya.core.runtime.intelligence.decision.quality.governance.history.DefaultRuntimeDecisionQualityGovernanceHistory
import pro.liliya.core.runtime.intelligence.decision.quality.governance.trend.DefaultRuntimeDecisionQualityGovernanceTrendQuery
import pro.liliya.core.runtime.intelligence.decision.quality.governance.trend.RuntimeDecisionQualityGovernanceTrendState
import pro.liliya.core.runtime.intelligence.decision.quality.summary.RuntimeDecisionQualitySummary
import pro.liliya.core.runtime.intelligence.decision.quality.summary.RuntimeDecisionQualitySummaryState
import pro.liliya.core.runtime.intelligence.decision.quality.trend.RuntimeDecisionQualityTrend
import pro.liliya.core.runtime.intelligence.decision.quality.trend.RuntimeDecisionQualityTrendState

class DefaultRuntimeDecisionQualityGovernanceTrendQueryContractTest {

    @Test
    fun query_reads_current_bounded_history_snapshot() {
        val history =
            DefaultRuntimeDecisionQualityGovernanceHistory(
                capacity = 2
            )

        history.record(
            assessment(
                RuntimeDecisionQualityGovernanceState.REVIEW
            )
        )

        history.record(
            assessment(
                RuntimeDecisionQualityGovernanceState.CAUTION
            )
        )

        history.record(
            assessment(
                RuntimeDecisionQualityGovernanceState.CLEAR
            )
        )

        val trend =
            DefaultRuntimeDecisionQualityGovernanceTrendQuery(
                history = history
            )
                .currentTrend()

        assertEquals(
            2,
            trend.sampleCount
        )

        assertEquals(
            RuntimeDecisionQualityGovernanceState.CAUTION,
            trend.firstGovernanceState
        )

        assertEquals(
            RuntimeDecisionQualityGovernanceState.CLEAR,
            trend.latestGovernanceState
        )

        assertEquals(
            RuntimeDecisionQualityGovernanceTrendState.IMPROVING,
            trend.state
        )
    }

    @Test
    fun clear_immediately_returns_insufficient_trend() {
        val history =
            DefaultRuntimeDecisionQualityGovernanceHistory()

        history.record(
            assessment(
                RuntimeDecisionQualityGovernanceState.CLEAR
            )
        )

        history.record(
            assessment(
                RuntimeDecisionQualityGovernanceState.CLEAR
            )
        )

        val query =
            DefaultRuntimeDecisionQualityGovernanceTrendQuery(
                history = history
            )

        history.clear()

        assertEquals(
            RuntimeDecisionQualityGovernanceTrendState
                .INSUFFICIENT_DATA,
            query.currentTrend().state
        )
    }

    private fun assessment(
        state:
            RuntimeDecisionQualityGovernanceState
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
                                        "governance query fixture"
                                ),
                            actionRecommended = false,
                            humanReviewRecommended =
                                state ==
                                    RuntimeDecisionQualityGovernanceState
                                        .REVIEW,
                            reason =
                                "governance query fixture"
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
                        "governance query fixture"
                ),
            proceedWithCaution =
                state ==
                    RuntimeDecisionQualityGovernanceState.CAUTION,
            reviewRecommended =
                state ==
                    RuntimeDecisionQualityGovernanceState.REVIEW,
            reason =
                "governance query fixture"
        )
}
