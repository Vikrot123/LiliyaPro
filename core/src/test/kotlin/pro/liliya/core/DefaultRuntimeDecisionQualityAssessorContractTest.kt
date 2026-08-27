package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.decision.quality.DefaultRuntimeDecisionQualityAssessor
import pro.liliya.core.runtime.intelligence.decision.quality.RuntimeDecisionQualityState
import pro.liliya.core.runtime.intelligence.decision.reflection.RuntimeDecisionReflectionEvidence
import pro.liliya.core.runtime.intelligence.decision.reflection.RuntimeDecisionReflectionInsight
import pro.liliya.core.runtime.intelligence.decision.reflection.trend.RuntimeDecisionReflectionTrend
import pro.liliya.core.runtime.intelligence.decision.reflection.trend.RuntimeDecisionReflectionTrendState

class DefaultRuntimeDecisionQualityAssessorContractTest {

    private val assessor =
        DefaultRuntimeDecisionQualityAssessor()

    @Test
    fun trustworthy_stable_decision_is_trustworthy() {
        val assessment =
            assessor.assess(
                reflection =
                    reflection(
                        trustworthy = true,
                        attention = false
                    ),
                trend =
                    trend(
                        state =
                            RuntimeDecisionReflectionTrendState.STABLE
                    )
            )

        assertEquals(
            RuntimeDecisionQualityState.TRUSTWORTHY,
            assessment.state
        )

        assertTrue(
            assessment.trustworthyKnowledgeBasis
        )

        assertTrue(
            assessment.temporallyStable
        )

        assertFalse(
            assessment.requiresAttention
        )
    }

    @Test
    fun oscillating_quality_is_unstable_even_when_current_basis_is_trustworthy() {
        val assessment =
            assessor.assess(
                reflection =
                    reflection(
                        trustworthy = true,
                        attention = false
                    ),
                trend =
                    trend(
                        state =
                            RuntimeDecisionReflectionTrendState.STABLE,
                        oscillating = true
                    )
            )

        assertEquals(
            RuntimeDecisionQualityState.UNSTABLE,
            assessment.state
        )

        assertFalse(
            assessment.temporallyStable
        )
    }

    @Test
    fun degrading_trend_requires_attention() {
        val assessment =
            assessor.assess(
                reflection =
                    reflection(
                        trustworthy = true,
                        attention = false
                    ),
                trend =
                    trend(
                        state =
                            RuntimeDecisionReflectionTrendState.DEGRADING
                    )
            )

        assertEquals(
            RuntimeDecisionQualityState.REQUIRES_ATTENTION,
            assessment.state
        )

        assertTrue(
            assessment.requiresAttention
        )
    }

    @Test
    fun current_attention_has_priority_over_temporal_state() {
        val assessment =
            assessor.assess(
                reflection =
                    reflection(
                        trustworthy = false,
                        attention = true
                    ),
                trend =
                    trend(
                        state =
                            RuntimeDecisionReflectionTrendState.IMPROVING
                    )
            )

        assertEquals(
            RuntimeDecisionQualityState.REQUIRES_ATTENTION,
            assessment.state
        )
    }

    @Test
    fun insufficient_history_remains_insufficient_when_current_reflection_is_trustworthy() {
        val assessment =
            assessor.assess(
                reflection =
                    reflection(
                        trustworthy = true,
                        attention = false
                    ),
                trend =
                    trend(
                        state =
                            RuntimeDecisionReflectionTrendState
                                .INSUFFICIENT_DATA,
                        sampleCount = 1
                    )
            )

        assertEquals(
            RuntimeDecisionQualityState.INSUFFICIENT_DATA,
            assessment.state
        )
    }

    private fun reflection(
        trustworthy: Boolean,
        attention: Boolean
    ) =
        RuntimeDecisionReflectionInsight(
            evidence =
                RuntimeDecisionReflectionEvidence(
                    command = null,
                    decisionReason =
                        "quality assessment",
                    confidence = 0.90,
                    knowledgeUsed =
                        trustworthy || attention,
                    provenanceAvailable =
                        trustworthy,
                    provenanceValid =
                        if (trustworthy) {
                            true
                        } else if (attention) {
                            false
                        } else {
                            null
                        },
                    provenanceDepth =
                        if (trustworthy) {
                            1
                        } else {
                            0
                        }
                ),
            trustworthyKnowledgeBasis =
                trustworthy,
            requiresAttention =
                attention,
            summary =
                "quality reflection"
        )

    private fun trend(
        state: RuntimeDecisionReflectionTrendState,
        sampleCount: Int = 3,
        oscillating: Boolean = false
    ) =
        RuntimeDecisionReflectionTrend(
            state = state,
            sampleCount = sampleCount,
            trustworthyRatio = 1.0,
            attentionRatio = 0.0,
            averageConfidence = 0.90,
            firstConfidence = 0.90,
            latestConfidence = 0.90,
            confidenceDelta = 0.0,
            qualityTransitionCount =
                if (oscillating) 2 else 0,
            confidenceDirectionChanges = 0,
            oscillating = oscillating
        )
}
