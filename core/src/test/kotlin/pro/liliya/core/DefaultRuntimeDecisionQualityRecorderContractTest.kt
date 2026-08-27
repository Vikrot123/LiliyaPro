package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import pro.liliya.core.runtime.intelligence.decision.quality.DefaultRuntimeDecisionQualityRecorder
import pro.liliya.core.runtime.intelligence.decision.quality.RuntimeDecisionQualityAssessment
import pro.liliya.core.runtime.intelligence.decision.quality.RuntimeDecisionQualityQuery
import pro.liliya.core.runtime.intelligence.decision.quality.RuntimeDecisionQualityState
import pro.liliya.core.runtime.intelligence.decision.quality.history.DefaultRuntimeDecisionQualityHistory
import pro.liliya.core.runtime.intelligence.decision.reflection.RuntimeDecisionReflectionEvidence
import pro.liliya.core.runtime.intelligence.decision.reflection.RuntimeDecisionReflectionInsight
import pro.liliya.core.runtime.intelligence.decision.reflection.trend.RuntimeDecisionReflectionTrend
import pro.liliya.core.runtime.intelligence.decision.reflection.trend.RuntimeDecisionReflectionTrendState

class DefaultRuntimeDecisionQualityRecorderContractTest {

    @Test
    fun missing_current_assessment_records_nothing() {
        val history =
            DefaultRuntimeDecisionQualityHistory()

        val recorder =
            DefaultRuntimeDecisionQualityRecorder(
                query =
                    object : RuntimeDecisionQualityQuery {
                        override fun currentAssessment():
                            RuntimeDecisionQualityAssessment? =
                            null
                    },
                history = history
            )

        assertNull(
            recorder.recordCurrent()
        )

        assertEquals(
            emptyList(),
            history.records()
        )
    }

    @Test
    fun current_assessment_is_recorded_exactly_once() {
        val history =
            DefaultRuntimeDecisionQualityHistory()

        val assessment =
            assessment()

        val recorder =
            DefaultRuntimeDecisionQualityRecorder(
                query =
                    object : RuntimeDecisionQualityQuery {
                        override fun currentAssessment():
                            RuntimeDecisionQualityAssessment =
                            assessment
                    },
                history = history
            )

        val record =
            recorder.recordCurrent()
                ?: error(
                    "quality record expected"
                )

        assertSame(
            assessment,
            record.assessment
        )

        assertEquals(
            1,
            history.records().size
        )
    }

    private fun assessment() =
        RuntimeDecisionQualityAssessment(
            state =
                RuntimeDecisionQualityState.TRUSTWORTHY,
            reflection =
                RuntimeDecisionReflectionInsight(
                    evidence =
                        RuntimeDecisionReflectionEvidence(
                            command = null,
                            decisionReason =
                                "quality recorder",
                            confidence = 0.90,
                            knowledgeUsed = true,
                            provenanceAvailable = true,
                            provenanceValid = true,
                            provenanceDepth = 1
                        ),
                    trustworthyKnowledgeBasis = true,
                    requiresAttention = false,
                    summary =
                        "quality recorder"
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
            trustworthyKnowledgeBasis = true,
            requiresAttention = false,
            temporallyStable = true,
            confidence = 0.90,
            reason =
                "quality recorder"
        )
}
