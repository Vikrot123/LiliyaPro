package pro.liliya.core.runtime.intelligence.intent

import pro.liliya.core.runtime.intelligence.goal.RuntimeGoalState
import pro.liliya.core.runtime.intelligence.reasoning.RuntimeReasoningState
import pro.liliya.core.runtime.intelligence.reasoning.orchestration.RuntimeAutonomousReasoningResult

class DefaultRuntimeIntentDeriver :
    RuntimeIntentDeriver {

    override fun derive(
        reasoningResult: RuntimeAutonomousReasoningResult
    ): RuntimeIntent {

        val reasoning =
            reasoningResult.reasoning

        if (
            reasoning.state !=
            RuntimeReasoningState.COHERENT
        ) {
            return RuntimeIntent(
                state =
                    RuntimeIntentState.WITHHOLD,
                objective =
                    "Withhold autonomous intent until reasoning is coherent",
                reasoningResult =
                    reasoningResult,
                coherent = false,
                actionable = false,
                confidence =
                    reasoningResult.confidence,
                reason =
                    "Autonomous reasoning is not coherent enough to form an actionable intent"
            )
        }

        val state =
            when (reasoningResult.goal.state) {
                RuntimeGoalState.OBSERVE ->
                    RuntimeIntentState.OBSERVE

                RuntimeGoalState.MAINTAIN ->
                    RuntimeIntentState.PRESERVE

                RuntimeGoalState.INVESTIGATE ->
                    RuntimeIntentState.INVESTIGATE

                RuntimeGoalState.RECOVER ->
                    RuntimeIntentState.RESTORE
            }

        val objective =
            when (state) {
                RuntimeIntentState.OBSERVE ->
                    "Observe runtime state and reduce uncertainty"

                RuntimeIntentState.PRESERVE ->
                    "Preserve stable runtime operation"

                RuntimeIntentState.INVESTIGATE ->
                    "Investigate runtime degradation"

                RuntimeIntentState.RESTORE ->
                    "Restore healthy runtime operation"

                RuntimeIntentState.WITHHOLD ->
                    "Withhold autonomous intent"
            }

        return RuntimeIntent(
            state = state,
            objective = objective,
            reasoningResult =
                reasoningResult,
            coherent = true,
            actionable =
                reasoningResult.actionable,
            confidence =
                reasoningResult.confidence,
            reason =
                "Intent derived from coherent autonomous reasoning"
        )
    }
}
