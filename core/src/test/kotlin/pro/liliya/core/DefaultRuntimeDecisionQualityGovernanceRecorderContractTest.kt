package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.RuntimeDecisionQualityAdvisory
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.RuntimeDecisionQualityAdvisoryLevel
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.summary.RuntimeDecisionQualityAdvisorySummary
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.summary.RuntimeDecisionQualityAdvisorySummaryState
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.trend.RuntimeDecisionQualityAdvisoryTrend
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.trend.RuntimeDecisionQualityAdvisoryTrendState
import pro.liliya.core.runtime.intelligence.decision.quality.governance.DefaultRuntimeDecisionQualityGovernanceRecorder
import pro.liliya.core.runtime.intelligence.decision.quality.governance.RuntimeDecisionQualityGovernanceAssessment
import pro.liliya.core.runtime.intelligence.decision.quality.governance.RuntimeDecisionQualityGovernanceQuery
import pro.liliya.core.runtime.intelligence.decision.quality.governance.RuntimeDecisionQualityGovernanceState
import pro.liliya.core.runtime.intelligence.decision.quality.governance.history.DefaultRuntimeDecisionQualityGovernanceHistory
import pro.liliya.core.runtime.intelligence.decision.quality.summary.RuntimeDecisionQualitySummary
import pro.liliya.core.runtime.intelligence.decision.quality.summary.RuntimeDecisionQualitySummaryState
import pro.liliya.core.runtime.intelligence.decision.quality.trend.RuntimeDecisionQualityTrend
import pro.liliya.core.runtime.intelligence.decision.quality.trend.RuntimeDecisionQualityTrendState

class DefaultRuntimeDecisionQualityGovernanceRecorderContractTest {

    @Test
    fun record_current_snapshots_query_result_exactly_once() {
        val assessment =
            assessment()

        var calls = 0

        val query =
            object : RuntimeDecisionQualityGovernanceQuery {
                override fun currentAssessment():
                    RuntimeDecisionQualityGovernanceAssessment {

                    calls += 1

                    return assessment
                }
            }

        val history =
            DefaultRuntimeDecisionQualityGovernanceHistory()

        val recorder =
            DefaultRuntimeDecisionQualityGovernanceRecorder(
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
            assessment,
            record.assessment
        )

        assertEquals(
            listOf(record),
            history.records()
        )
    }

    private fun assessment() =
        RuntimeDecisionQualityGovernanceAssessment(
            state =
                RuntimeDecisionQualityGovernanceState.CAUTION,
            advisorySummary =
                RuntimeDecisionQualityAdvisorySummary(
                    state =
                        RuntimeDecisionQualityAdvisorySummaryState
                            .DEGRADING,
                    advisory =
                        RuntimeDecisionQualityAdvisory(
                            level =
                                RuntimeDecisionQualityAdvisoryLevel
                                    .CAUTION,
                            summary =
                                RuntimeDecisionQualitySummary(
                                    state =
                                        RuntimeDecisionQualitySummaryState
                                            .WATCH,
                                    assessment = null,
                                    trend =
                                        RuntimeDecisionQualityTrend(
                                            state =
                                                RuntimeDecisionQualityTrendState
                                                    .DEGRADING,
                                            sampleCount = 2,
                                            firstQualityState = null,
                                            latestQualityState = null,
                                            trustworthyRatio = 0.0,
                                            unstableRatio = 0.0,
                                            attentionRatio = 0.0,
                                            qualityTransitionCount = 1,
                                            directionChanges = 0,
                                            oscillating = false
                                        ),
                                    trustworthy = false,
                                    requiresAttention = false,
                                    temporallyStable = false,
                                    oscillating = false,
                                    reason =
                                        "governance recorder fixture"
                                ),
                            actionRecommended = true,
                            humanReviewRecommended = false,
                            reason =
                                "governance recorder fixture"
                        ),
                    trend =
                        RuntimeDecisionQualityAdvisoryTrend(
                            state =
                                RuntimeDecisionQualityAdvisoryTrendState
                                    .DEGRADING,
                            sampleCount = 2,
                            firstLevel =
                                RuntimeDecisionQualityAdvisoryLevel
                                    .PROCEED,
                            latestLevel =
                                RuntimeDecisionQualityAdvisoryLevel
                                    .CAUTION,
                            proceedRatio = 0.5,
                            cautionRatio = 0.5,
                            reviewRatio = 0.0,
                            advisoryTransitionCount = 1,
                            directionChanges = 0,
                            oscillating = false
                        ),
                    stable = false,
                    oscillating = false,
                    requiresReview = false,
                    reason =
                        "governance recorder fixture"
                ),
            proceedWithCaution = true,
            reviewRecommended = false,
            reason =
                "governance recorder fixture"
        )
}
