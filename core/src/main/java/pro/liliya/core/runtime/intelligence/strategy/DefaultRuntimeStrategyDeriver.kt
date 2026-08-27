package pro.liliya.core.runtime.intelligence.strategy

import pro.liliya.core.runtime.intelligence.intent.RuntimeIntent
import pro.liliya.core.runtime.intelligence.intent.RuntimeIntentState

class DefaultRuntimeStrategyDeriver :
    RuntimeStrategyDeriver {

    override fun derive(
        intent: RuntimeIntent
    ): RuntimeStrategy {

        val reasoningResult =
            intent.reasoningResult

        val upstreamInconsistent =
            !reasoningResult.coherent ||
                (
                    intent.actionable &&
                        !reasoningResult.actionable
                )

        if (
            !intent.coherent ||
            intent.state == RuntimeIntentState.WITHHOLD ||
            upstreamInconsistent
        ) {
            return RuntimeStrategy(
                state =
                    RuntimeStrategyState.DEFER,
                objective =
                    "Defer autonomous strategy until intent is coherent",
                intent =
                    intent,
                coherent =
                    false,
                actionable =
                    false,
                confidence =
                    intent.confidence,
                reason =
                    "Autonomous intent cannot safely form an active strategy"
            )
        }

        val state =
            when (intent.state) {
                RuntimeIntentState.OBSERVE ->
                    RuntimeStrategyState.MONITOR

                RuntimeIntentState.PRESERVE ->
                    RuntimeStrategyState.PRESERVE

                RuntimeIntentState.INVESTIGATE ->
                    RuntimeStrategyState.DIAGNOSE

                RuntimeIntentState.RESTORE ->
                    RuntimeStrategyState.RESTORE

                RuntimeIntentState.WITHHOLD ->
                    RuntimeStrategyState.DEFER
            }

        val objective =
            when (state) {
                RuntimeStrategyState.MONITOR ->
                    "Monitor runtime state and reduce uncertainty"

                RuntimeStrategyState.PRESERVE ->
                    "Preserve current healthy runtime conditions"

                RuntimeStrategyState.DIAGNOSE ->
                    "Diagnose runtime degradation before intervention"

                RuntimeStrategyState.RESTORE ->
                    "Prepare a coherent restoration approach"

                RuntimeStrategyState.DEFER ->
                    "Defer autonomous strategy"
            }

        return RuntimeStrategy(
            state =
                state,
            objective =
                objective,
            intent =
                intent,
            coherent =
                true,
            actionable =
                intent.actionable,
            confidence =
                intent.confidence,
            reason =
                "Strategy derived from coherent autonomous intent"
        )
    }
}
