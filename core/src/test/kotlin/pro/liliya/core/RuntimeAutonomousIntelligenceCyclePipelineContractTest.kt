package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.autonomous.DefaultRuntimeAutonomousIntelligenceCyclePipeline
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceCommitState
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.orchestration.RuntimeIntelligenceOrchestrator
import pro.liliya.core.runtime.intelligence.orchestration.RuntimeIntelligenceOrchestrationResult

class RuntimeAutonomousIntelligenceCyclePipelineContractTest {

    @Test
    fun integration_runs_intelligence_before_autonomous_execution_cycle() {
        val composition =
            DefaultRuntimeComposition()

        val intelligence =
            RuntimeIntelligenceFixture.result(
                significance =
                    RuntimeMeaningSignificance.WARNING,
                confidence =
                    0.90
            )

        var orchestratorCalls = 0

        val orchestrator =
            object : RuntimeIntelligenceOrchestrator {

                override fun process():
                    RuntimeIntelligenceOrchestrationResult {

                    orchestratorCalls += 1

                    return intelligence
                }
            }

        val pipeline =
            DefaultRuntimeAutonomousIntelligenceCyclePipeline(
                intelligenceOrchestrator =
                    orchestrator,
                executionCyclePipeline =
                    composition.autonomousExecutionCyclePipeline()
            )

        val result =
            pipeline.process(
                source =
                    "v1.498-complete",
                authority =
                    RuntimeActionAuthorityContext(
                        source =
                            "v1.498-complete",
                        level =
                            RuntimeAuthorityLevel.SYSTEM
                    )
            )

        assertEquals(
            1,
            orchestratorCalls
        )

        assertSame(
            intelligence,
            result.intelligence
        )

        assertSame(
            intelligence,
            result.executionCycle.execution.intelligence
        )

        assertSame(
            result.executionCycle.execution,
            result.executionCycle.evaluation.executionResult
        )

        assertSame(
            result.executionCycle.evaluation,
            result.executionCycle.postExecution.evaluation
        )

        val commit =
            assertNotNull(
                result.executionCycle.postExecution.commitResult
            )

        assertEquals(
            RuntimeAutonomousExecutionExperienceCommitState.COMMITTED,
            commit.state
        )
    }

    @Test
    fun integration_forwards_exact_source_and_authority_semantics() {
        val composition =
            DefaultRuntimeComposition()

        val intelligence =
            RuntimeIntelligenceFixture.result(
                significance =
                    RuntimeMeaningSignificance.WARNING,
                confidence =
                    0.90
            )

        val pipeline =
            DefaultRuntimeAutonomousIntelligenceCyclePipeline(
                intelligenceOrchestrator =
                    object : RuntimeIntelligenceOrchestrator {

                        override fun process():
                            RuntimeIntelligenceOrchestrationResult {

                            return intelligence
                        }
                    },
                executionCyclePipeline =
                    composition.autonomousExecutionCyclePipeline()
            )

        val authority =
            RuntimeActionAuthorityContext(
                source =
                    "v1.498-authority",
                level =
                    RuntimeAuthorityLevel.SYSTEM
            )

        val result =
            pipeline.process(
                source =
                    "v1.498-authority",
                authority =
                    authority
            )

        assertSame(
            intelligence,
            result.intelligence
        )

        assertSame(
            intelligence,
            result.executionCycle.execution.intelligence
        )

        val request =
            assertNotNull(
                result.executionCycle.execution.execution.request
            )

        assertEquals(
            "v1.498-authority",
            request.source
        )

        assertSame(
            authority,
            request.authority
        )
    }

    @Test
    fun integration_invokes_each_top_level_stage_exactly_once() {
        val composition =
            DefaultRuntimeComposition()

        val intelligence =
            RuntimeIntelligenceFixture.result(
                significance =
                    RuntimeMeaningSignificance.STABLE,
                confidence =
                    0.95
            )

        var intelligenceCalls = 0

        val orchestrator =
            object : RuntimeIntelligenceOrchestrator {

                override fun process():
                    RuntimeIntelligenceOrchestrationResult {

                    intelligenceCalls += 1

                    return intelligence
                }
            }

        val pipeline =
            DefaultRuntimeAutonomousIntelligenceCyclePipeline(
                intelligenceOrchestrator =
                    orchestrator,
                executionCyclePipeline =
                    composition.autonomousExecutionCyclePipeline()
            )

        val result =
            pipeline.process(
                source =
                    "v1.498-cardinality",
                authority =
                    RuntimeActionAuthorityContext(
                        source =
                            "v1.498-cardinality",
                        level =
                            RuntimeAuthorityLevel.SYSTEM
                    )
            )

        assertEquals(
            1,
            intelligenceCalls
        )

        assertSame(
            intelligence,
            result.intelligence
        )

        assertSame(
            intelligence,
            result.executionCycle.execution.intelligence
        )
    }
}
