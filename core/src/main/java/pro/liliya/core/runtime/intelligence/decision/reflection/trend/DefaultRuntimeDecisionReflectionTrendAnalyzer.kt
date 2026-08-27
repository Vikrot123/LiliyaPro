package pro.liliya.core.runtime.intelligence.decision.reflection.trend

import kotlin.math.abs
import pro.liliya.core.runtime.intelligence.decision.reflection.history.RuntimeDecisionReflectionRecord

class DefaultRuntimeDecisionReflectionTrendAnalyzer :
    RuntimeDecisionReflectionTrendAnalyzer {

    override fun analyze(
        records: List<RuntimeDecisionReflectionRecord>
    ): RuntimeDecisionReflectionTrend {

        if (records.isEmpty()) {
            return RuntimeDecisionReflectionTrend(
                state =
                    RuntimeDecisionReflectionTrendState
                        .INSUFFICIENT_DATA,
                sampleCount = 0,
                trustworthyRatio = 0.0,
                attentionRatio = 0.0,
                averageConfidence = 0.0,
                firstConfidence = null,
                latestConfidence = null,
                confidenceDelta = 0.0
            )
        }

        val trustworthyCount =
            records.count {
                it.insight.trustworthyKnowledgeBasis
            }

        val attentionCount =
            records.count {
                it.insight.requiresAttention
            }

        val trustworthyRatio =
            trustworthyCount.toDouble() /
                records.size.toDouble()

        val attentionRatio =
            attentionCount.toDouble() /
                records.size.toDouble()

        val confidences =
            records.map {
                it.insight.evidence.confidence
            }

        val averageConfidence =
            confidences.average()

        val firstConfidence =
            confidences.first()

        val latestConfidence =
            confidences.last()

        val confidenceDelta =
            latestConfidence - firstConfidence

        val state =
            classify(
                records = records,
                trustworthyRatio = trustworthyRatio,
                attentionRatio = attentionRatio,
                confidenceDelta = confidenceDelta
            )

        return RuntimeDecisionReflectionTrend(
            state = state,
            sampleCount = records.size,
            trustworthyRatio = trustworthyRatio,
            attentionRatio = attentionRatio,
            averageConfidence = averageConfidence,
            firstConfidence = firstConfidence,
            latestConfidence = latestConfidence,
            confidenceDelta = confidenceDelta
        )
    }

    private fun classify(
        records: List<RuntimeDecisionReflectionRecord>,
        trustworthyRatio: Double,
        attentionRatio: Double,
        confidenceDelta: Double
    ): RuntimeDecisionReflectionTrendState {

        if (records.size < MIN_SAMPLE_COUNT) {
            return RuntimeDecisionReflectionTrendState
                .INSUFFICIENT_DATA
        }

        val first =
            records.first().insight

        val latest =
            records.last().insight

        val trustImproved =
            !first.trustworthyKnowledgeBasis &&
                latest.trustworthyKnowledgeBasis

        val attentionImproved =
            first.requiresAttention &&
                !latest.requiresAttention

        val trustDegraded =
            first.trustworthyKnowledgeBasis &&
                !latest.trustworthyKnowledgeBasis

        val attentionDegraded =
            !first.requiresAttention &&
                latest.requiresAttention

        return when {
            trustImproved ||
                attentionImproved ||
                confidenceDelta >= CONFIDENCE_CHANGE_THRESHOLD ->
                RuntimeDecisionReflectionTrendState.IMPROVING

            trustDegraded ||
                attentionDegraded ||
                confidenceDelta <= -CONFIDENCE_CHANGE_THRESHOLD ->
                RuntimeDecisionReflectionTrendState.DEGRADING

            attentionRatio >= ATTENTION_DEGRADING_RATIO ->
                RuntimeDecisionReflectionTrendState.DEGRADING

            trustworthyRatio >= TRUSTWORTHY_STABLE_RATIO ->
                RuntimeDecisionReflectionTrendState.STABLE

            abs(confidenceDelta) <
                CONFIDENCE_CHANGE_THRESHOLD ->
                RuntimeDecisionReflectionTrendState.STABLE

            else ->
                RuntimeDecisionReflectionTrendState.STABLE
        }
    }

    companion object {
        const val MIN_SAMPLE_COUNT = 2

        const val CONFIDENCE_CHANGE_THRESHOLD =
            0.10

        const val TRUSTWORTHY_STABLE_RATIO =
            0.75

        const val ATTENTION_DEGRADING_RATIO =
            0.50
    }
}
