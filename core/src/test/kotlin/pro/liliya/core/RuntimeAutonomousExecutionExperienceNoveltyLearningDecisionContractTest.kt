package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.DefaultRuntimeAutonomousExecutionExperienceLearningDecisionEngine
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.DefaultRuntimeAutonomousExecutionExperienceNoveltyDeriver
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceLearningDecisionState
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeAutonomousExecutionExperienceNoveltyLearningDecisionContractTest {

    @Test
    fun novel_post_execution_information_becomes_learning_candidate() {
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
                    source = "v1.489-learning-novel",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "v1.489-learning-novel",
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
            pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection
                .DefaultRuntimeAutonomousExecutionReflectionAnalyzer(
                    reflectionAnalyzer =
                        composition.decisionReflectionAnalyzer()
                )
                .analyze(
                    evaluation = evaluation,
                    feedback = feedback
                )

        val novelty =
            DefaultRuntimeAutonomousExecutionExperienceNoveltyDeriver()
                .derive(
                    feedback
                )

        val decision =
            DefaultRuntimeAutonomousExecutionExperienceLearningDecisionEngine()
                .decide(
                    analysis = analysis,
                    novelty = novelty
                )

        assertEquals(
            RuntimeAutonomousExecutionExperienceLearningDecisionState.PROCESS_NOVEL_EXPERIENCE,
            decision.state
        )

        assertTrue(
            decision.shouldProcessExperience
        )
    }

    @Test
    fun no_action_remains_already_represented() {
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
                    source = "v1.489-learning-existing",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "v1.489-learning-existing",
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
            pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection
                .DefaultRuntimeAutonomousExecutionReflectionAnalyzer(
                    reflectionAnalyzer =
                        composition.decisionReflectionAnalyzer()
                )
                .analyze(
                    evaluation = evaluation,
                    feedback = feedback
                )

        val novelty =
            DefaultRuntimeAutonomousExecutionExperienceNoveltyDeriver()
                .derive(
                    feedback
                )

        val decision =
            DefaultRuntimeAutonomousExecutionExperienceLearningDecisionEngine()
                .decide(
                    analysis = analysis,
                    novelty = novelty
                )

        assertEquals(
            RuntimeAutonomousExecutionExperienceLearningDecisionState.ALREADY_REPRESENTED,
            decision.state
        )

        assertFalse(
            decision.shouldProcessExperience
        )
    }
}
