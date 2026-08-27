package pro.liliya.core.runtime.intelligence.decision.quality.trend

import pro.liliya.core.runtime.intelligence.decision.quality.RuntimeDecisionQualityState
import pro.liliya.core.runtime.intelligence.decision.quality.history.RuntimeDecisionQualityRecord

class DefaultRuntimeDecisionQualityTrendAnalyzer :
    RuntimeDecisionQualityTrendAnalyzer {

    override fun analyze(
        records: List<RuntimeDecisionQualityRecord>
    ): RuntimeDecisionQualityTrend {

        if (records.isEmpty()) {
            return emptyTrend()
        }

        val states =
            records.map {
                it.assessment.state
            }

        val sampleCount =
            states.size

        val trustworthyRatio =
            ratio(
                states.count {
                    it ==
                        RuntimeDecisionQualityState.TRUSTWORTHY
                },
                sampleCount
            )

        val unstableRatio =
            ratio(
                states.count {
                    it ==
                        RuntimeDecisionQualityState.UNSTABLE
                },
                sampleCount
            )

        val attentionRatio =
            ratio(
                states.count {
                    it ==
                        RuntimeDecisionQualityState
                            .REQUIRES_ATTENTION
                },
                sampleCount
            )

        val transitionCount =
            states
                .zipWithNext()
                .count { (first, second) ->
                    first != second
                }

        val directions =
            states
                .zipWithNext()
                .mapNotNull { (first, second) ->
                    direction(
                        first,
                        second
                    )
                }

        val directionChanges =
            directions
                .zipWithNext()
                .count { (first, second) ->
                    first != second
                }

        val oscillating =
            sampleCount >= MIN_OSCILLATION_SAMPLE_COUNT &&
                directionChanges > 0

        val trendState =
            classify(
                states = states,
                oscillating = oscillating
            )

        return RuntimeDecisionQualityTrend(
            state = trendState,
            sampleCount = sampleCount,
            firstQualityState =
                states.firstOrNull(),
            latestQualityState =
                states.lastOrNull(),
            trustworthyRatio =
                trustworthyRatio,
            unstableRatio =
                unstableRatio,
            attentionRatio =
                attentionRatio,
            qualityTransitionCount =
                transitionCount,
            directionChanges =
                directionChanges,
            oscillating =
                oscillating
        )
    }

    private fun classify(
        states: List<RuntimeDecisionQualityState>,
        oscillating: Boolean
    ): RuntimeDecisionQualityTrendState {

        if (states.size < MIN_SAMPLE_COUNT) {
            return RuntimeDecisionQualityTrendState
                .INSUFFICIENT_DATA
        }

        if (oscillating) {
            return RuntimeDecisionQualityTrendState.STABLE
        }

        val first =
            states.first()

        val latest =
            states.last()

        val firstScore =
            score(first)

        val latestScore =
            score(latest)

        return when {
            latestScore > firstScore ->
                RuntimeDecisionQualityTrendState.IMPROVING

            latestScore < firstScore ->
                RuntimeDecisionQualityTrendState.DEGRADING

            else ->
                RuntimeDecisionQualityTrendState.STABLE
        }
    }

    private fun direction(
        first: RuntimeDecisionQualityState,
        second: RuntimeDecisionQualityState
    ): Int? {

        val delta =
            score(second) - score(first)

        return when {
            delta > 0 -> 1
            delta < 0 -> -1
            else -> null
        }
    }

    private fun score(
        state: RuntimeDecisionQualityState
    ): Int {

        return when (state) {
            RuntimeDecisionQualityState.REQUIRES_ATTENTION ->
                0

            RuntimeDecisionQualityState.UNSTABLE ->
                1

            RuntimeDecisionQualityState.INSUFFICIENT_DATA ->
                2

            RuntimeDecisionQualityState.TRUSTWORTHY ->
                3
        }
    }

    private fun ratio(
        count: Int,
        total: Int
    ): Double {

        if (total == 0) {
            return 0.0
        }

        return count.toDouble() /
            total.toDouble()
    }

    private fun emptyTrend() =
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
        )

    companion object {
        const val MIN_SAMPLE_COUNT = 2
        const val MIN_OSCILLATION_SAMPLE_COUNT = 3
    }
}
