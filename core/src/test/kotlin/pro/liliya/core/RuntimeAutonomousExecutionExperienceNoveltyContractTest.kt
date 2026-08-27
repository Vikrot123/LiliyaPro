package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.DefaultRuntimeAutonomousExecutionExperienceNoveltyDeriver
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceNoveltyState
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeAutonomousExecutionExperienceNoveltyContractTest {

    private val deriver =
        DefaultRuntimeAutonomousExecutionExperienceNoveltyDeriver()

    @Test
    fun successful_executed_action_is_new_post_execution_information() {
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
                    source = "v1.489-success",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "v1.489-success",
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
                .derive(evaluation.assessment)

        val novelty =
            deriver.derive(
                feedback
            )

        assertEquals(
            RuntimeAutonomousExecutionExperienceNoveltyState.NOVEL,
            novelty.state
        )

        assertTrue(
            novelty.novel
        )

        assertTrue(
            novelty.actionAttempted
        )

        assertEquals(
            true,
            novelty.actionSucceeded
        )
    }

    @Test
    fun failed_executed_action_is_new_post_execution_information() {
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
                    source = "v1.489-failed",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "v1.489-failed",
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
                .derive(evaluation.assessment)

        val novelty =
            deriver.derive(
                feedback
            )

        assertEquals(
            RuntimeAutonomousExecutionExperienceNoveltyState.NOVEL,
            novelty.state
        )

        assertTrue(
            novelty.novel
        )

        assertTrue(
            novelty.actionAttempted
        )

        assertEquals(
            false,
            novelty.actionSucceeded
        )
    }

    @Test
    fun no_action_contains_no_new_execution_experience() {
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
                    source = "v1.489-no-action",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "v1.489-no-action",
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
                .derive(evaluation.assessment)

        val novelty =
            deriver.derive(
                feedback
            )

        assertEquals(
            RuntimeAutonomousExecutionExperienceNoveltyState.ALREADY_REPRESENTED,
            novelty.state
        )

        assertFalse(
            novelty.novel
        )

        assertFalse(
            novelty.actionAttempted
        )
    }
}
