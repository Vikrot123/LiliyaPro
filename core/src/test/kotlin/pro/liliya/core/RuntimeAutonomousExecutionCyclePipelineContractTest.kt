package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.cycle.DefaultRuntimeAutonomousExecutionCyclePipeline
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceCommitState
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeAutonomousExecutionCyclePipelineContractTest {

    @Test
    fun warning_intelligence_flows_through_exact_complete_autonomous_cycle() {
        val composition =
            DefaultRuntimeComposition()

        val pipeline =
            DefaultRuntimeAutonomousExecutionCyclePipeline(
                executionPipeline =
                    composition.autonomousExecutionPipeline(),
                evaluationPipeline =
                    composition.autonomousExecutionEvaluationPipeline(),
                postExecutionLearningPipeline =
                    composition.autonomousPostExecutionLearningPipeline()
            )

        val intelligence =
            RuntimeIntelligenceFixture.result(
                significance =
                    RuntimeMeaningSignificance.WARNING,
                confidence =
                    0.90
            )

        val result =
            pipeline.process(
                intelligence =
                    intelligence,
                source =
                    "v1.496-complete",
                authority =
                    RuntimeActionAuthorityContext(
                        source =
                            "v1.496-complete",
                        level =
                            RuntimeAuthorityLevel.SYSTEM
                    )
            )

        assertSame(
            intelligence,
            result.execution.intelligence
        )

        assertSame(
            result.execution,
            result.evaluation.executionResult
        )

        assertSame(
            result.evaluation,
            result.postExecution.evaluation
        )

        val commit =
            assertNotNull(
                result.postExecution.commitResult
            )

        assertEquals(
            RuntimeAutonomousExecutionExperienceCommitState.COMMITTED,
            commit.state
        )

        assertTrue(
            commit.committed
        )
    }

    @Test
    fun stable_intelligence_completes_cycle_without_experience_commit() {
        val composition =
            DefaultRuntimeComposition()

        val store =
            composition
                .experienceComposition()
                .experienceStore()

        val pipeline =
            DefaultRuntimeAutonomousExecutionCyclePipeline(
                executionPipeline =
                    composition.autonomousExecutionPipeline(),
                evaluationPipeline =
                    composition.autonomousExecutionEvaluationPipeline(),
                postExecutionLearningPipeline =
                    composition.autonomousPostExecutionLearningPipeline()
            )

        val before =
            store.experiences().size

        val result =
            pipeline.process(
                intelligence =
                    RuntimeIntelligenceFixture.result(
                        significance =
                            RuntimeMeaningSignificance.STABLE,
                        confidence =
                            0.95
                    ),
                source =
                    "v1.496-stable",
                authority =
                    RuntimeActionAuthorityContext(
                        source =
                            "v1.496-stable",
                        level =
                            RuntimeAuthorityLevel.SYSTEM
                    )
            )

        assertNull(
            result.postExecution.materialization
        )

        assertNull(
            result.postExecution.representation
        )

        assertNull(
            result.postExecution.commitDecision
        )

        assertNull(
            result.postExecution.commitResult
        )

        assertEquals(
            before,
            store.experiences().size
        )
    }

    @Test
    fun cycle_preserves_exact_stage_identity_chain() {
        val composition =
            DefaultRuntimeComposition()

        val pipeline =
            DefaultRuntimeAutonomousExecutionCyclePipeline(
                executionPipeline =
                    composition.autonomousExecutionPipeline(),
                evaluationPipeline =
                    composition.autonomousExecutionEvaluationPipeline(),
                postExecutionLearningPipeline =
                    composition.autonomousPostExecutionLearningPipeline()
            )

        val result =
            pipeline.process(
                intelligence =
                    RuntimeIntelligenceFixture.result(
                        significance =
                            RuntimeMeaningSignificance.WARNING,
                        confidence =
                            0.90
                    ),
                source =
                    "v1.496-identity",
                authority =
                    RuntimeActionAuthorityContext(
                        source =
                            "v1.496-identity",
                        level =
                            RuntimeAuthorityLevel.SYSTEM
                    )
            )

        assertSame(
            result.execution,
            result.evaluation.executionResult
        )

        assertSame(
            result.evaluation,
            result.postExecution.evaluation
        )

        assertSame(
            result.postExecution.feedback,
            result.postExecution.analysis.evidence.feedback
        )

        assertSame(
            result.postExecution.analysis,
            result.postExecution.materialization?.analysis
        )
    }
}
