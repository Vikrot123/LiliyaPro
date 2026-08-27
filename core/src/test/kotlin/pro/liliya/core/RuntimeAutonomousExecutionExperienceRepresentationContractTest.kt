package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.DefaultRuntimeAutonomousExecutionExperienceMaterializer
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.DefaultRuntimeAutonomousExecutionExperienceNoveltyDeriver
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.DefaultRuntimeAutonomousExecutionExperienceRepresentationDeriver
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection.DefaultRuntimeAutonomousExecutionReflectionAnalyzer
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceImportance
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeAutonomousExecutionExperienceRepresentationContractTest {

    @Test
    fun post_execution_representation_preserves_exact_materialization_snapshot() {
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
                    source = "v1.491",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "v1.491",
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
            DefaultRuntimeAutonomousExecutionExperienceMaterializer()
                .materialize(
                    analysis = analysis,
                    novelty = novelty
                )
                ?: error("novel execution must materialize")

        val representation =
            DefaultRuntimeAutonomousExecutionExperienceRepresentationDeriver()
                .derive(
                    materialization
                )

        assertSame(
            materialization,
            representation.materialization
        )

        assertEquals(
            materialization.description,
            representation.experience.description
        )
    }

    @Test
    fun representation_preserves_originating_meaning_identity() {
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
                    source = "v1.491-meaning",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "v1.491-meaning",
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
            DefaultRuntimeAutonomousExecutionExperienceMaterializer()
                .materialize(
                    analysis = analysis,
                    novelty = novelty
                )
                ?: error("novel execution must materialize")

        val representation =
            DefaultRuntimeAutonomousExecutionExperienceRepresentationDeriver()
                .derive(
                    materialization
                )

        assertSame(
            execution.intelligence.meaning,
            representation.experience.meaning
        )

        assertEquals(
            RuntimeExperienceImportance.MEDIUM,
            representation.experience.importance
        )
    }

    @Test
    fun representation_does_not_erase_post_execution_semantics() {
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
                    source = "v1.491-failure",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "v1.491-failure",
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
            DefaultRuntimeAutonomousExecutionExperienceMaterializer()
                .materialize(
                    analysis = analysis,
                    novelty = novelty
                )
                ?: error("failed action remains novel information")

        val representation =
            DefaultRuntimeAutonomousExecutionExperienceRepresentationDeriver()
                .derive(
                    materialization
                )

        assertSame(
            materialization.analysis,
            representation.materialization.analysis
        )

        assertSame(
            materialization.novelty,
            representation.materialization.novelty
        )

        assertEquals(
            false,
            representation.materialization.actionSucceeded
        )

        assertTrue(
            representation.experience.description.isNotBlank()
        )
    }
}
