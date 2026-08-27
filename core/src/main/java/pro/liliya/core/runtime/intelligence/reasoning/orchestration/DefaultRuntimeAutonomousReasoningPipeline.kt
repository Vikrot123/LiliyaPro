package pro.liliya.core.runtime.intelligence.reasoning.orchestration

import pro.liliya.core.runtime.intelligence.goal.RuntimeGoalDeriver
import pro.liliya.core.runtime.intelligence.orchestration.RuntimeIntelligenceOrchestrationResult
import pro.liliya.core.runtime.intelligence.planning.RuntimePlanner
import pro.liliya.core.runtime.intelligence.reasoning.RuntimeReasoningAnalyzer

class DefaultRuntimeAutonomousReasoningPipeline(
    private val goalDeriver: RuntimeGoalDeriver,
    private val planner: RuntimePlanner,
    private val reasoningAnalyzer: RuntimeReasoningAnalyzer
) : RuntimeAutonomousReasoningPipeline {

    override fun process(
        intelligence: RuntimeIntelligenceOrchestrationResult
    ): RuntimeAutonomousReasoningResult {

        val goal =
            goalDeriver.derive(
                intelligence
            )

        val plan =
            planner.plan(
                goal
            )

        val reasoning =
            reasoningAnalyzer.analyze(
                goal = goal,
                plan = plan
            )

        return RuntimeAutonomousReasoningResult(
            goal = goal,
            plan = plan,
            reasoning = reasoning,
            coherent =
                reasoning.coherent,
            actionable =
                reasoning.actionable,
            confidence =
                reasoning.confidence
        )
    }
}
