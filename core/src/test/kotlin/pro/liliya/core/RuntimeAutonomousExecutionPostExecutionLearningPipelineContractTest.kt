package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.DefaultRuntimeAutonomousExecutionExperienceCommitDecisionEngine
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.DefaultRuntimeAutonomousExecutionExperienceCommitter
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.DefaultRuntimeAutonomousExecutionExperienceLearningDecisionEngine
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.DefaultRuntimeAutonomousExecutionExperienceMaterializer
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.DefaultRuntimeAutonomousExecutionExperienceNoveltyDeriver
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.DefaultRuntimeAutonomousExecutionExperienceRepresentationDeriver
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceCommitState
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceLearningDecisionState
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.postexecution.DefaultRuntimeAutonomousExecutionPostExecutionLearningPipeline
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection.DefaultRuntimeAutonomousExecutionReflectionAnalyzer
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection.DefaultRuntimeAutonomousExecutionReflectionRecorder
import pro.liliya.core.runtime.intelligence.experience.decision.DefaultRuntimeExperienceDecisionEngine
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeAutonomousExecutionPostExecutionLearningPipelineContractTest {

    @Test
    fun successful_autonomous_execution_flows_through_complete_post_execution_learning_chain() {
        val composition =
            DefaultRuntimeComposition()

        val store =
            composition
                .experienceComposition()
                .experienceStore()

        val pipeline =
            pipeline(
                composition
            )

        val execution =
            composition
                .autonomousExecutionPipeline()
                .process(
                    intelligence =
                        RuntimeIntelligenceFixture.result(
                            significance =
                                RuntimeMeaningSignificance.WARNING,
                            confidence =
                                0.90
                        ),
                    source =
                        "v1.494-complete-chain",
                    authority =
                        RuntimeActionAuthorityContext(
                            source =
                                "v1.494-complete-chain",
                            level =
                                RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val evaluation =
            composition
                .autonomousExecutionEvaluationPipeline()
                .evaluate(execution)

        val before =
            store.experiences().size

        val result =
            pipeline.process(
                evaluation
            )

        assertSame(
            evaluation,
            result.evaluation
        )

        assertEquals(
            evaluation.assessment.state,
            result.feedback.assessmentState
        )

        assertSame(
            result.feedback,
            result.analysis.evidence.feedback
        )

        assertTrue(
            result.novelty.novel
        )

        assertEquals(
            RuntimeAutonomousExecutionExperienceLearningDecisionState.PROCESS_NOVEL_EXPERIENCE,
            result.learningDecision.state
        )

        val materialization =
            assertNotNull(
                result.materialization
            )

        val representation =
            assertNotNull(
                result.representation
            )

        val commitDecision =
            assertNotNull(
                result.commitDecision
            )

        val commitResult =
            assertNotNull(
                result.commitResult
            )

        assertSame(
            result.analysis,
            materialization.analysis
        )

        assertSame(
            materialization,
            representation.materialization
        )

        assertSame(
            representation,
            commitDecision.representation
        )

        assertSame(
            representation.experience,
            commitResult.experience
        )

        assertEquals(
            RuntimeAutonomousExecutionExperienceCommitState.COMMITTED,
            commitResult.state
        )

        assertTrue(
            commitResult.committed
        )

        assertEquals(
            before + 1,
            store.experiences().size
        )

        assertSame(
            representation.experience,
            store.experiences().last()
        )
    }

    @Test
    fun no_action_execution_stops_before_materialization_and_commit() {
        val composition =
            DefaultRuntimeComposition()

        val store =
            composition
                .experienceComposition()
                .experienceStore()

        val pipeline =
            pipeline(
                composition
            )

        val execution =
            composition
                .autonomousExecutionPipeline()
                .process(
                    intelligence =
                        RuntimeIntelligenceFixture.result(
                            significance =
                                RuntimeMeaningSignificance.STABLE,
                            confidence =
                                0.95
                        ),
                    source =
                        "v1.494-no-action",
                    authority =
                        RuntimeActionAuthorityContext(
                            source =
                                "v1.494-no-action",
                            level =
                                RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val evaluation =
            composition
                .autonomousExecutionEvaluationPipeline()
                .evaluate(execution)

        val before =
            store.experiences().size

        val result =
            pipeline.process(
                evaluation
            )

        assertSame(
            evaluation,
            result.evaluation
        )

        assertFalse(
            result.novelty.novel
        )

        assertEquals(
            RuntimeAutonomousExecutionExperienceLearningDecisionState.ALREADY_REPRESENTED,
            result.learningDecision.state
        )

        assertNull(
            result.materialization
        )

        assertNull(
            result.representation
        )

        assertNull(
            result.commitDecision
        )

        assertNull(
            result.commitResult
        )

        assertEquals(
            before,
            store.experiences().size
        )
    }

    @Test
    fun pipeline_records_reflection_only_through_existing_reflection_history() {
        val composition =
            DefaultRuntimeComposition()

        val pipeline =
            pipeline(
                composition
            )

        val history =
            composition
                .decisionReflectionHistory()

        val execution =
            composition
                .autonomousExecutionPipeline()
                .process(
                    intelligence =
                        RuntimeIntelligenceFixture.result(
                            significance =
                                RuntimeMeaningSignificance.WARNING,
                            confidence =
                                0.90
                        ),
                    source =
                        "v1.494-reflection",
                    authority =
                        RuntimeActionAuthorityContext(
                            source =
                                "v1.494-reflection",
                            level =
                                RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val evaluation =
            composition
                .autonomousExecutionEvaluationPipeline()
                .evaluate(execution)

        val before =
            history.records().size

        val result =
            pipeline.process(
                evaluation
            )

        val record =
            assertNotNull(
                result.reflectionRecord
            )

        assertEquals(
            before + 1,
            history.records().size
        )

        assertSame(
            record,
            history.records().last()
        )

        assertSame(
            result.analysis.insight,
            record.insight
        )
    }

    private fun pipeline(
        composition: DefaultRuntimeComposition
    ) =
        DefaultRuntimeAutonomousExecutionPostExecutionLearningPipeline(
            feedbackDeriver =
                composition
                    .autonomousExecutionFeedbackDeriver(),
            reflectionAnalyzer =
                DefaultRuntimeAutonomousExecutionReflectionAnalyzer(
                    reflectionAnalyzer =
                        composition
                            .decisionReflectionAnalyzer()
                ),
            reflectionRecorder =
                DefaultRuntimeAutonomousExecutionReflectionRecorder(
                    history =
                        composition
                            .decisionReflectionHistory()
                ),
            noveltyDeriver =
                DefaultRuntimeAutonomousExecutionExperienceNoveltyDeriver(),
            learningDecisionEngine =
                DefaultRuntimeAutonomousExecutionExperienceLearningDecisionEngine(),
            materializer =
                DefaultRuntimeAutonomousExecutionExperienceMaterializer(),
            representationDeriver =
                DefaultRuntimeAutonomousExecutionExperienceRepresentationDeriver(),
            commitDecisionEngine =
                DefaultRuntimeAutonomousExecutionExperienceCommitDecisionEngine(
                    experienceDecisionEngine =
                        DefaultRuntimeExperienceDecisionEngine()
                ),
            committer =
                DefaultRuntimeAutonomousExecutionExperienceCommitter(
                    experienceStore =
                        composition
                            .experienceComposition()
                            .experienceStore()
                )
        )
}
