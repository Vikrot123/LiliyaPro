package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.decision.reflection.RuntimeDecisionReflectionEvidence
import pro.liliya.core.runtime.intelligence.decision.reflection.RuntimeDecisionReflectionInsight
import pro.liliya.core.runtime.intelligence.decision.reflection.history.RuntimeDecisionReflectionRecord
import pro.liliya.core.runtime.intelligence.decision.reflection.trend.DefaultRuntimeDecisionReflectionTrendAnalyzer
import pro.liliya.core.runtime.intelligence.decision.reflection.trend.RuntimeDecisionReflectionTrendState

class DefaultRuntimeDecisionReflectionTrendAnalyzerContractTest {

    private val analyzer =
        DefaultRuntimeDecisionReflectionTrendAnalyzer()

    @Test
    fun empty_history_is_insufficient_data() {
        val trend =
            analyzer.analyze(
                emptyList()
            )

        assertEquals(
            RuntimeDecisionReflectionTrendState
                .INSUFFICIENT_DATA,
            trend.state
        )

        assertEquals(
            0,
            trend.sampleCount
        )

        assertEquals(
            0.0,
            trend.averageConfidence
        )
    }

    @Test
    fun one_record_is_insufficient_data() {
        val trend =
            analyzer.analyze(
                listOf(
                    record(
                        confidence = 0.80,
                        trustworthy = true,
                        attention = false,
                        recordedAt = 1L
                    )
                )
            )

        assertEquals(
            RuntimeDecisionReflectionTrendState
                .INSUFFICIENT_DATA,
            trend.state
        )

        assertEquals(
            1,
            trend.sampleCount
        )
    }

    @Test
    fun stable_trustworthy_history_is_stable() {
        val trend =
            analyzer.analyze(
                listOf(
                    record(
                        confidence = 0.90,
                        trustworthy = true,
                        attention = false,
                        recordedAt = 1L
                    ),
                    record(
                        confidence = 0.92,
                        trustworthy = true,
                        attention = false,
                        recordedAt = 2L
                    ),
                    record(
                        confidence = 0.91,
                        trustworthy = true,
                        attention = false,
                        recordedAt = 3L
                    )
                )
            )

        assertEquals(
            RuntimeDecisionReflectionTrendState.STABLE,
            trend.state
        )

        assertEquals(
            1.0,
            trend.trustworthyRatio
        )

        assertEquals(
            0.0,
            trend.attentionRatio
        )
    }

    @Test
    fun trust_improvement_is_detected() {
        val trend =
            analyzer.analyze(
                listOf(
                    record(
                        confidence = 0.60,
                        trustworthy = false,
                        attention = true,
                        recordedAt = 1L
                    ),
                    record(
                        confidence = 0.85,
                        trustworthy = true,
                        attention = false,
                        recordedAt = 2L
                    )
                )
            )

        assertEquals(
            RuntimeDecisionReflectionTrendState.IMPROVING,
            trend.state
        )

        assertTrue(
            trend.confidenceDelta > 0.0
        )
    }

    @Test
    fun trust_degradation_is_detected() {
        val trend =
            analyzer.analyze(
                listOf(
                    record(
                        confidence = 0.90,
                        trustworthy = true,
                        attention = false,
                        recordedAt = 1L
                    ),
                    record(
                        confidence = 0.65,
                        trustworthy = false,
                        attention = true,
                        recordedAt = 2L
                    )
                )
            )

        assertEquals(
            RuntimeDecisionReflectionTrendState.DEGRADING,
            trend.state
        )

        assertTrue(
            trend.confidenceDelta < 0.0
        )
    }

    private fun record(
        confidence: Double,
        trustworthy: Boolean,
        attention: Boolean,
        recordedAt: Long
    ) =
        RuntimeDecisionReflectionRecord(
            insight =
                RuntimeDecisionReflectionInsight(
                    evidence =
                        RuntimeDecisionReflectionEvidence(
                            command = null,
                            decisionReason =
                                "trend evidence",
                            confidence =
                                confidence,
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
                        "trend insight"
                ),
            recordedAt =
                recordedAt
        )
}
