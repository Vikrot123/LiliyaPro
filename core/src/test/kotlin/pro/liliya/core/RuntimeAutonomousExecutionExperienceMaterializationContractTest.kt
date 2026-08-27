package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.DefaultRuntimeAutonomousExecutionExperienceMaterializer
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.DefaultRuntimeAutonomousExecutionExperienceNoveltyDeriver
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceMaterializationState
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection.DefaultRuntimeAutonomousExecutionReflectionAnalyzer
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeAutonomousExecutionExperienceMaterializationContractTest {

    private val materializer =
        DefaultRuntimeAutonomousExecutionExperienceMaterializer()

    @Test
    fun successful_execution_materializes_exact_post_execution_semantics() {
        val composition =
            DefaultRuntimeComposition()

        val execution =
            composition
                .autonomousExecutionPipeline()
                .process(
                    intelligence =
                        RuntimeIntelligenceFixture.result(
                            significance =
                                RuntimeMeaningSignificance.WARNING,
                            confidence = 0.80
                        ),
                    source = "v1.490-success",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "v1.490-success",
                            level = RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val evaluation =
            composition
                .autonomousExecutionEvaluationPipeline()
                .evaluate(execution)

        val feedback =
            composition
                .autonomousExecutionFeedbackDeriver()
                .derive(
                    evaluation.assessment
                )

        val analysis =
            DefaultRuntimeAutonomousExecutionReflectionAnalyzer(
                reflectionAnalyzer =
                    composition.decisionReflectionAnalyzer()
            ).analyze(
                evaluation = evaluation,
                feedback = feedback
            )

        val novelty =
            DefaultRuntimeAutonomousExecutionExperienceNoveltyDeriver()
                .derive(
                    feedback
                )

        val materialization =
            materializer.materialize(
                analysis = analysis,
                novelty = novelty
            )
                ?: error("novel execution must materialize")

        assertEquals(
            RuntimeAutonomousExecutionExperienceMaterializationState.MATERIALIZED,
            materialization.state
        )

        assertSame(
            analysis,
            materialization.analysis
        )

        assertSame(
            novelty,
            materialization.novelty
        )

        assertEquals(
            feedback.command,
            materialization.command
        )

        assertEquals(
            true,
            materialization.actionSucceeded
        )

        assertEquals(
            feedback.previousRuntimeState,
            materialization.previousRuntimeState
        )

        assertEquals(
            feedback.currentRuntimeState,
            materialization.currentRuntimeState
        )

        assertEquals(
            feedback.message,
            materialization.message
        )

        assertTrue(
            materialization.description.isNotBlank()
        )
    }

    @Test
    fun failed_execution_is_still_materializable_new_information() {
        val composition =
            DefaultRuntimeComposition()

        val execution =
            composition
                .autonomousExecutionPipeline()
                .process(
                    intelligence =
                        RuntimeIntelligenceFixture.result(
                            significance =
                                RuntimeMeaningSignificance.CRITICAL,
                            confidence = 0.95
                        ),
                    source = "v1.490-failure",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "v1.490-failure",
                            level = RuntimeAuthorityLevel.USER
                        )
                )

        val evaluation =
            composition
                .autonomousExecutionEvaluationPipeline()
                .evaluate(execution)

        val feedback =
            composition
                .autonomousExecutionFeedbackDeriver()
                .derive(
                    evaluation.assessment
                )

        val analysis =
            DefaultRuntimeAutonomousExecutionReflectionAnalyzer(
                reflectionAnalyzer =
                    composition.decisionReflectionAnalyzer()
            ).analyze(
                evaluation = evaluation,
                feedback = feedback
            )

        val novelty =
            DefaultRuntimeAutonomousExecutionExperienceNoveltyDeriver()
                .derive(
                    feedback
                )

        val materialization =
            materializer.materialize(
                analysis = analysis,
                novelty = novelty
            )
                ?: error("failed action is still novel post-execution information")

        assertEquals(
            false,
            materialization.actionSucceeded
        )

        assertTrue(
            materialization.description.isNotBlank()
        )
    }

    @Test
    fun no_action_does_not_materialize_duplicate_experience() {
        val composition =
            DefaultRuntimeComposition()

        val execution =
            composition
                .autonomousExecutionPipeline()
                .process(
                    intelligence =
                        RuntimeIntelligenceFixture.result(
                            significance =
                                RuntimeMeaningSignificance.STABLE,
                            confidence = 0.90
                        ),
                    source = "v1.490-no-action",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "v1.490-no-action",
                            level = RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val evaluation =
            composition
                .autonomousExecutionEvaluationPipeline()
                .evaluate(execution)

        val feedback =
            composition
                .autonomousExecutionFeedbackDeriver()
                .derive(
                    evaluation.assessment
                )

        val analysis =
            DefaultRuntimeAutonomousExecutionReflectionAnalyzer(
                reflectionAnalyzer =
                    composition.decisionReflectionAnalyzer()
            ).analyze(
                evaluation = evaluation,
                feedback = feedback
            )

        val novelty =
            DefaultRuntimeAutonomousExecutionExperienceNoveltyDeriver()
                .derive(
                    feedback
                )

        assertNull(
            materializer.materialize(
                analysis = analysis,
                novelty = novelty
            )
        )
    }
}
