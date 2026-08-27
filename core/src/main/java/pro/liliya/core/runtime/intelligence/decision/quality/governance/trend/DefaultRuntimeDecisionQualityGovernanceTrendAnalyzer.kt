package pro.liliya.core.runtime.intelligence.decision.quality.governance.trend

import pro.liliya.core.runtime.intelligence.decision.quality.governance.RuntimeDecisionQualityGovernanceState
import pro.liliya.core.runtime.intelligence.decision.quality.governance.history.RuntimeDecisionQualityGovernanceRecord

class DefaultRuntimeDecisionQualityGovernanceTrendAnalyzer :
    RuntimeDecisionQualityGovernanceTrendAnalyzer {

    override fun analyze(
        records:
            List<RuntimeDecisionQualityGovernanceRecord>
    ): RuntimeDecisionQualityGovernanceTrend {

        val states =
            records.map {
                it.assessment.state
            }

        if (states.isEmpty()) {
            return emptyTrend()
        }

        val transitions =
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
            directionChanges > 0

        return RuntimeDecisionQualityGovernanceTrend(
            state =
                classify(
                    states = states,
                    oscillating = oscillating
                ),
            sampleCount =
                states.size,
            firstGovernanceState =
                states.first(),
            latestGovernanceState =
                states.last(),
            clearRatio =
                ratio(
                    states.count {
                        it ==
                            RuntimeDecisionQualityGovernanceState
                                .CLEAR
                    },
                    states.size
                ),
            cautionRatio =
                ratio(
                    states.count {
                        it ==
                            RuntimeDecisionQualityGovernanceState
                                .CAUTION
                    },
                    states.size
                ),
            reviewRatio =
                ratio(
                    states.count {
                        it ==
                            RuntimeDecisionQualityGovernanceState
                                .REVIEW
                    },
                    states.size
                ),
            governanceTransitionCount =
                transitions,
            directionChanges =
                directionChanges,
            oscillating =
                oscillating
        )
    }

    private fun classify(
        states:
            List<RuntimeDecisionQualityGovernanceState>,
        oscillating: Boolean
    ): RuntimeDecisionQualityGovernanceTrendState {

        if (states.size < MIN_SAMPLE_COUNT) {
            return RuntimeDecisionQualityGovernanceTrendState
                .INSUFFICIENT_DATA
        }

        if (oscillating) {
            return RuntimeDecisionQualityGovernanceTrendState
                .STABLE
        }

        val firstScore =
            score(
                states.first()
            )

        val latestScore =
            score(
                states.last()
            )

        return when {
            latestScore > firstScore ->
                RuntimeDecisionQualityGovernanceTrendState
                    .IMPROVING

            latestScore < firstScore ->
                RuntimeDecisionQualityGovernanceTrendState
                    .DEGRADING

            else ->
                RuntimeDecisionQualityGovernanceTrendState
                    .STABLE
        }
    }

    private fun direction(
        first:
            RuntimeDecisionQualityGovernanceState,
        second:
            RuntimeDecisionQualityGovernanceState
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
        state:
            RuntimeDecisionQualityGovernanceState
    ): Int {

        return when (state) {
            RuntimeDecisionQualityGovernanceState.REVIEW ->
                0

            RuntimeDecisionQualityGovernanceState.CAUTION ->
                1

            RuntimeDecisionQualityGovernanceState
                .INSUFFICIENT_DATA ->
                2

            RuntimeDecisionQualityGovernanceState.CLEAR ->
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
        RuntimeDecisionQualityGovernanceTrend(
            state =
                RuntimeDecisionQualityGovernanceTrendState
                    .INSUFFICIENT_DATA,
            sampleCount = 0,
            firstGovernanceState = null,
            latestGovernanceState = null,
            clearRatio = 0.0,
            cautionRatio = 0.0,
            reviewRatio = 0.0,
            governanceTransitionCount = 0,
            directionChanges = 0,
            oscillating = false
        )

    companion object {
        const val MIN_SAMPLE_COUNT = 2
    }
}
