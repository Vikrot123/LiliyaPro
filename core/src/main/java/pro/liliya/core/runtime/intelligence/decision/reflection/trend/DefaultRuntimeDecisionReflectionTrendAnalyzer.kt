package pro.liliya.core.runtime.intelligence.decision.reflection.trend

import kotlin.math.abs
import pro.liliya.core.runtime.intelligence.decision.reflection.RuntimeDecisionReflectionInsight
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
                confidenceDelta = 0.0,
                qualityTransitionCount = 0,
                confidenceDirectionChanges = 0,
                oscillating = false
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

        val qualityTransitionCount =
            countQualityTransitions(
                records
            )

        val confidenceDirectionChanges =
            countConfidenceDirectionChanges(
                confidences
            )

        val oscillating =
            records.size >= MIN_OSCILLATION_SAMPLE_COUNT &&
                (
                    qualityTransitionCount >=
                        MIN_QUALITY_TRANSITIONS_FOR_OSCILLATION ||
                        confidenceDirectionChanges >=
                            MIN_DIRECTION_CHANGES_FOR_OSCILLATION
                )

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
            confidenceDelta = confidenceDelta,
            qualityTransitionCount =
                qualityTransitionCount,
            confidenceDirectionChanges =
                confidenceDirectionChanges,
            oscillating =
                oscillating
        )
    }

    private fun countQualityTransitions(
        records: List<RuntimeDecisionReflectionRecord>
    ): Int {

        val states =
            records.map {
                qualityState(
                    it.insight
                )
            }

        return states
            .zipWithNext()
            .count { pair ->
                pair.first != pair.second
            }
    }

    private fun qualityState(
        insight: RuntimeDecisionReflectionInsight
    ): QualityState {

        return when {
            insight.requiresAttention ->
                QualityState.ATTENTION

            insight.trustworthyKnowledgeBasis ->
                QualityState.TRUSTWORTHY

            else ->
                QualityState.NEUTRAL
        }
    }

    private fun countConfidenceDirectionChanges(
        confidences: List<Double>
    ): Int {

        val directions =
            confidences
                .zipWithNext()
                .map { pair ->
                    confidenceDirection(
                        pair.second - pair.first
                    )
                }
                .filter {
                    it != ConfidenceDirection.FLAT
                }

        return directions
            .zipWithNext()
            .count { pair ->
                pair.first != pair.second
            }
    }

    private fun confidenceDirection(
        delta: Double
    ): ConfidenceDirection {

        return when {
            delta >= SIGNIFICANT_CONFIDENCE_STEP ->
                ConfidenceDirection.UP

            delta <= -SIGNIFICANT_CONFIDENCE_STEP ->
                ConfidenceDirection.DOWN

            else ->
                ConfidenceDirection.FLAT
        }
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

    private enum class QualityState {
        TRUSTWORTHY,
        ATTENTION,
        NEUTRAL
    }

    private enum class ConfidenceDirection {
        UP,
        DOWN,
        FLAT
    }

    companion object {
        const val MIN_SAMPLE_COUNT = 2

        const val MIN_OSCILLATION_SAMPLE_COUNT = 3

        const val CONFIDENCE_CHANGE_THRESHOLD =
            0.10

        const val SIGNIFICANT_CONFIDENCE_STEP =
            0.01

        const val TRUSTWORTHY_STABLE_RATIO =
            0.75

        const val ATTENTION_DEGRADING_RATIO =
            0.50

        const val MIN_QUALITY_TRANSITIONS_FOR_OSCILLATION =
            2

        const val MIN_DIRECTION_CHANGES_FOR_OSCILLATION =
            1
    }
}
