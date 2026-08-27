package pro.liliya.core.runtime.intelligence.decision.quality.advisory.trend

import pro.liliya.core.runtime.intelligence.decision.quality.advisory.RuntimeDecisionQualityAdvisoryLevel
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.history.RuntimeDecisionQualityAdvisoryRecord

class DefaultRuntimeDecisionQualityAdvisoryTrendAnalyzer :
    RuntimeDecisionQualityAdvisoryTrendAnalyzer {

    override fun analyze(
        records: List<RuntimeDecisionQualityAdvisoryRecord>
    ): RuntimeDecisionQualityAdvisoryTrend {

        val levels =
            records.map {
                it.advisory.level
            }

        if (levels.isEmpty()) {
            return emptyTrend()
        }

        val transitions =
            levels
                .zipWithNext()
                .count { (first, second) ->
                    first != second
                }

        val directions =
            levels
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
            directionChanges > 0

        return RuntimeDecisionQualityAdvisoryTrend(
            state =
                classify(
                    levels = levels,
                    oscillating = oscillating
                ),
            sampleCount =
                levels.size,
            firstLevel =
                levels.first(),
            latestLevel =
                levels.last(),
            proceedRatio =
                ratio(
                    levels.count {
                        it ==
                            RuntimeDecisionQualityAdvisoryLevel
                                .PROCEED
                    },
                    levels.size
                ),
            cautionRatio =
                ratio(
                    levels.count {
                        it ==
                            RuntimeDecisionQualityAdvisoryLevel
                                .CAUTION
                    },
                    levels.size
                ),
            reviewRatio =
                ratio(
                    levels.count {
                        it ==
                            RuntimeDecisionQualityAdvisoryLevel
                                .REVIEW
                    },
                    levels.size
                ),
            advisoryTransitionCount =
                transitions,
            directionChanges =
                directionChanges,
            oscillating =
                oscillating
        )
    }

    private fun classify(
        levels: List<RuntimeDecisionQualityAdvisoryLevel>,
        oscillating: Boolean
    ): RuntimeDecisionQualityAdvisoryTrendState {

        if (levels.size < MIN_SAMPLE_COUNT) {
            return RuntimeDecisionQualityAdvisoryTrendState
                .INSUFFICIENT_DATA
        }

        if (oscillating) {
            return RuntimeDecisionQualityAdvisoryTrendState.STABLE
        }

        val firstScore =
            score(
                levels.first()
            )

        val latestScore =
            score(
                levels.last()
            )

        return when {
            latestScore > firstScore ->
                RuntimeDecisionQualityAdvisoryTrendState.IMPROVING

            latestScore < firstScore ->
                RuntimeDecisionQualityAdvisoryTrendState.DEGRADING

            else ->
                RuntimeDecisionQualityAdvisoryTrendState.STABLE
        }
    }

    private fun direction(
        first: RuntimeDecisionQualityAdvisoryLevel,
        second: RuntimeDecisionQualityAdvisoryLevel
    ): Int? {

        val delta =
            score(second) -
                score(first)

        return when {
            delta > 0 -> 1
            delta < 0 -> -1
            else -> null
        }
    }

    private fun score(
        level: RuntimeDecisionQualityAdvisoryLevel
    ): Int {

        return when (level) {
            RuntimeDecisionQualityAdvisoryLevel.REVIEW ->
                0

            RuntimeDecisionQualityAdvisoryLevel.CAUTION ->
                1

            RuntimeDecisionQualityAdvisoryLevel.OBSERVE ->
                2

            RuntimeDecisionQualityAdvisoryLevel.PROCEED ->
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
        )

    companion object {
        const val MIN_SAMPLE_COUNT = 2
    }
}
