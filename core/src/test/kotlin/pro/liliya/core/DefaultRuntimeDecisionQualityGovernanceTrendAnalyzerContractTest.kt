package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.RuntimeDecisionQualityAdvisory
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.RuntimeDecisionQualityAdvisoryLevel
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.summary.RuntimeDecisionQualityAdvisorySummary
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.summary.RuntimeDecisionQualityAdvisorySummaryState
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.trend.RuntimeDecisionQualityAdvisoryTrend
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.trend.RuntimeDecisionQualityAdvisoryTrendState
import pro.liliya.core.runtime.intelligence.decision.quality.governance.RuntimeDecisionQualityGovernanceAssessment
import pro.liliya.core.runtime.intelligence.decision.quality.governance.RuntimeDecisionQualityGovernanceState
import pro.liliya.core.runtime.intelligence.decision.quality.governance.history.RuntimeDecisionQualityGovernanceRecord
import pro.liliya.core.runtime.intelligence.decision.quality.governance.trend.DefaultRuntimeDecisionQualityGovernanceTrendAnalyzer
import pro.liliya.core.runtime.intelligence.decision.quality.governance.trend.RuntimeDecisionQualityGovernanceTrendState
import pro.liliya.core.runtime.intelligence.decision.quality.summary.RuntimeDecisionQualitySummary
import pro.liliya.core.runtime.intelligence.decision.quality.summary.RuntimeDecisionQualitySummaryState
import pro.liliya.core.runtime.intelligence.decision.quality.trend.RuntimeDecisionQualityTrend
import pro.liliya.core.runtime.intelligence.decision.quality.trend.RuntimeDecisionQualityTrendState

class DefaultRuntimeDecisionQualityGovernanceTrendAnalyzerContractTest {

    private val analyzer =
        DefaultRuntimeDecisionQualityGovernanceTrendAnalyzer()

    @Test
    fun empty_history_is_insufficient() {
        val trend =
            analyzer.analyze(
                emptyList()
            )

        assertEquals(
            RuntimeDecisionQualityGovernanceTrendState
                .INSUFFICIENT_DATA,
            trend.state
        )

        assertEquals(
            0,
            trend.sampleCount
        )
    }

    @Test
    fun one_record_is_insufficient() {
        val trend =
            analyzer.analyze(
                listOf(
                    record(
                        RuntimeDecisionQualityGovernanceState.CLEAR
                    )
                )
            )

        assertEquals(
            RuntimeDecisionQualityGovernanceTrendState
                .INSUFFICIENT_DATA,
            trend.state
        )
    }

    @Test
    fun review_to_clear_is_improving() {
        val trend =
            analyzer.analyze(
                listOf(
                    record(
                        RuntimeDecisionQualityGovernanceState.REVIEW
                    ),
                    record(
                        RuntimeDecisionQualityGovernanceState.CLEAR
                    )
                )
            )

        assertEquals(
            RuntimeDecisionQualityGovernanceTrendState.IMPROVING,
            trend.state
        )
    }

    @Test
    fun clear_to_review_is_degrading() {
        val trend =
            analyzer.analyze(
                listOf(
                    record(
                        RuntimeDecisionQualityGovernanceState.CLEAR
                    ),
                    record(
                        RuntimeDecisionQualityGovernanceState.REVIEW
                    )
                )
            )

        assertEquals(
            RuntimeDecisionQualityGovernanceTrendState.DEGRADING,
            trend.state
        )
    }

    @Test
    fun insufficient_to_clear_is_improving() {
        val trend =
            analyzer.analyze(
                listOf(
                    record(
                        RuntimeDecisionQualityGovernanceState
                            .INSUFFICIENT_DATA
                    ),
                    record(
                        RuntimeDecisionQualityGovernanceState.CLEAR
                    )
                )
            )

        assertEquals(
            RuntimeDecisionQualityGovernanceTrendState.IMPROVING,
            trend.state
        )
    }

    @Test
    fun caution_to_insufficient_is_improving() {
        val trend =
            analyzer.analyze(
                listOf(
                    record(
                        RuntimeDecisionQualityGovernanceState.CAUTION
                    ),
                    record(
                        RuntimeDecisionQualityGovernanceState
                            .INSUFFICIENT_DATA
                    )
                )
            )

        assertEquals(
            RuntimeDecisionQualityGovernanceTrendState.IMPROVING,
            trend.state
        )
    }

    @Test
    fun repeated_same_state_is_stable() {
        val trend =
            analyzer.analyze(
                listOf(
                    record(
                        RuntimeDecisionQualityGovernanceState.CLEAR
                    ),
                    record(
                        RuntimeDecisionQualityGovernanceState.CLEAR
                    ),
                    record(
                        RuntimeDecisionQualityGovernanceState.CLEAR
                    )
                )
            )

        assertEquals(
            RuntimeDecisionQualityGovernanceTrendState.STABLE,
            trend.state
        )

        assertFalse(
            trend.oscillating
        )

        assertEquals(
            0,
            trend.governanceTransitionCount
        )
    }

    @Test
    fun alternating_governance_is_detected_as_oscillating() {
        val trend =
            analyzer.analyze(
                listOf(
                    record(
                        RuntimeDecisionQualityGovernanceState.CLEAR
                    ),
                    record(
                        RuntimeDecisionQualityGovernanceState.REVIEW
                    ),
                    record(
                        RuntimeDecisionQualityGovernanceState.CLEAR
                    ),
                    record(
                        RuntimeDecisionQualityGovernanceState.REVIEW
                    )
                )
            )

        assertTrue(
            trend.oscillating
        )

        assertTrue(
            trend.directionChanges > 0
        )

        assertEquals(
            RuntimeDecisionQualityGovernanceTrendState.STABLE,
            trend.state
        )
    }

    private fun record(
        state:
            RuntimeDecisionQualityGovernanceState
    ) =
        RuntimeDecisionQualityGovernanceRecord(
            assessment =
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
                                                "governance trend fixture"
                                        ),
                                    actionRecommended = false,
                                    humanReviewRecommended =
                                        state ==
                                            RuntimeDecisionQualityGovernanceState
                                                .REVIEW,
                                    reason =
                                        "governance trend fixture"
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
                                "governance trend fixture"
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
                        "governance trend fixture"
                ),
            recordedAt = 1L
        )
}
