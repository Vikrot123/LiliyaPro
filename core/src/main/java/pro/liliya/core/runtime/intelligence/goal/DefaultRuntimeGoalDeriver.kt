package pro.liliya.core.runtime.intelligence.goal

import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.orchestration.RuntimeIntelligenceOrchestrationResult

class DefaultRuntimeGoalDeriver :
    RuntimeGoalDeriver {

    override fun derive(
        intelligence: RuntimeIntelligenceOrchestrationResult
    ): RuntimeGoal {

        val meaning =
            intelligence.meaning

        return when (meaning.significance) {
            RuntimeMeaningSignificance.STABLE ->
                RuntimeGoal(
                    state =
                        RuntimeGoalState.MAINTAIN,
                    priority =
                        RuntimeGoalPriority.LOW,
                    objective =
                        "Maintain stable runtime operation",
                    confidence =
                        meaning.confidence,
                    actionable =
                        false
                )

            RuntimeMeaningSignificance.WARNING ->
                RuntimeGoal(
                    state =
                        RuntimeGoalState.INVESTIGATE,
                    priority =
                        RuntimeGoalPriority.HIGH,
                    objective =
                        "Investigate runtime degradation",
                    confidence =
                        meaning.confidence,
                    actionable =
                        true
                )

            RuntimeMeaningSignificance.CRITICAL ->
                RuntimeGoal(
                    state =
                        RuntimeGoalState.RECOVER,
                    priority =
                        RuntimeGoalPriority.CRITICAL,
                    objective =
                        "Restore healthy runtime operation",
                    confidence =
                        meaning.confidence,
                    actionable =
                        true
                )

            RuntimeMeaningSignificance.UNKNOWN ->
                RuntimeGoal(
                    state =
                        RuntimeGoalState.OBSERVE,
                    priority =
                        RuntimeGoalPriority.NORMAL,
                    objective =
                        "Reduce uncertainty about runtime state",
                    confidence =
                        meaning.confidence,
                    actionable =
                        true
                )
        }
    }
}
