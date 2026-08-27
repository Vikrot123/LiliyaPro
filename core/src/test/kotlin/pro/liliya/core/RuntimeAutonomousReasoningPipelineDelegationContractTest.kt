package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import pro.liliya.core.runtime.intelligence.goal.RuntimeGoal
import pro.liliya.core.runtime.intelligence.goal.RuntimeGoalDeriver
import pro.liliya.core.runtime.intelligence.goal.RuntimeGoalPriority
import pro.liliya.core.runtime.intelligence.goal.RuntimeGoalState
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.orchestration.RuntimeIntelligenceOrchestrationResult
import pro.liliya.core.runtime.intelligence.planning.RuntimePlan
import pro.liliya.core.runtime.intelligence.planning.RuntimePlanState
import pro.liliya.core.runtime.intelligence.planning.RuntimePlanStep
import pro.liliya.core.runtime.intelligence.planning.RuntimePlanStepType
import pro.liliya.core.runtime.intelligence.planning.RuntimePlanner
import pro.liliya.core.runtime.intelligence.reasoning.RuntimeReasoningAnalyzer
import pro.liliya.core.runtime.intelligence.reasoning.RuntimeReasoningAssessment
import pro.liliya.core.runtime.intelligence.reasoning.RuntimeReasoningState
import pro.liliya.core.runtime.intelligence.reasoning.orchestration.DefaultRuntimeAutonomousReasoningPipeline

class RuntimeAutonomousReasoningPipelineDelegationContractTest {

    @Test
    fun pipeline_invokes_each_autonomy_stage_exactly_once() {
        val intelligence =
            RuntimeIntelligenceFixture.result(
                significance =
                    RuntimeMeaningSignificance.WARNING,
                confidence = 0.80
            )

        val goal =
            RuntimeGoal(
                state =
                    RuntimeGoalState.INVESTIGATE,
                priority =
                    RuntimeGoalPriority.HIGH,
                objective =
                    "delegation goal",
                confidence = 0.80,
                actionable = true
            )

        val plan =
            RuntimePlan(
                goal = goal,
                state =
                    RuntimePlanState.INVESTIGATE,
                steps =
                    listOf(
                        RuntimePlanStep(
                            order = 1,
                            type =
                                RuntimePlanStepType.ASSESS_HEALTH,
                            objective =
                                "delegation step",
                            actionable = true
                        ),
                        RuntimePlanStep(
                            order = 2,
                            type =
                                RuntimePlanStepType
                                    .IDENTIFY_DEGRADATION,
                            objective =
                                "delegation diagnosis",
                            actionable = true
                        )
                    ),
                actionable = true,
                confidence = 0.80
            )

        val assessment =
            RuntimeReasoningAssessment(
                goal = goal,
                plan = plan,
                state =
                    RuntimeReasoningState.COHERENT,
                issues = emptyList(),
                coherent = true,
                actionable = true,
                confidence = 0.80,
                reason =
                    "delegation assessment"
            )

        var deriveCalls = 0
        var planCalls = 0
        var analyzeCalls = 0

        val pipeline =
            DefaultRuntimeAutonomousReasoningPipeline(
                goalDeriver =
                    object : RuntimeGoalDeriver {
                        override fun derive(
                            intelligence:
                                RuntimeIntelligenceOrchestrationResult
                        ): RuntimeGoal {
                            deriveCalls += 1
                            assertSame(
                                expectedIntelligence,
                                intelligence
                            )
                            return goal
                        }
                    },
                planner =
                    object : RuntimePlanner {
                        override fun plan(
                            goal: RuntimeGoal
                        ): RuntimePlan {
                            planCalls += 1
                            assertSame(
                                goal,
                                this@RuntimeAutonomousReasoningPipelineDelegationContractTest
                                    .expectedGoal
                            )
                            return plan
                        }
                    },
                reasoningAnalyzer =
                    object : RuntimeReasoningAnalyzer {
                        override fun analyze(
                            goal: RuntimeGoal,
                            plan: RuntimePlan
                        ): RuntimeReasoningAssessment {
                            analyzeCalls += 1
                            return assessment
                        }
                    }
            )

        expectedIntelligence =
            intelligence

        expectedGoal =
            goal

        val result =
            pipeline.process(
                intelligence
            )

        assertEquals(
            1,
            deriveCalls
        )

        assertEquals(
            1,
            planCalls
        )

        assertEquals(
            1,
            analyzeCalls
        )

        assertSame(
            goal,
            result.goal
        )

        assertSame(
            plan,
            result.plan
        )

        assertSame(
            assessment,
            result.reasoning
        )
    }

    private lateinit var expectedIntelligence:
        RuntimeIntelligenceOrchestrationResult

    private lateinit var expectedGoal:
        RuntimeGoal
}
