package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.DefaultRuntimeAutonomousExecutionExperienceCommitDecisionEngine
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.DefaultRuntimeAutonomousExecutionExperienceLearningDecisionEngine
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.DefaultRuntimeAutonomousExecutionExperienceMaterializer
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.DefaultRuntimeAutonomousExecutionExperienceNoveltyDeriver
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.DefaultRuntimeAutonomousExecutionExperienceRepresentationDeriver
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceCommitDecisionState
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceLearningDecision
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceLearningDecisionState
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection.DefaultRuntimeAutonomousExecutionReflectionAnalyzer
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceImportance
import pro.liliya.core.runtime.intelligence.experience.decision.DefaultRuntimeExperienceDecisionEngine
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeAutonomousExecutionExperienceCommitDecisionContractTest {

    private val commitDecisionEngine =
        DefaultRuntimeAutonomousExecutionExperienceCommitDecisionEngine(
            experienceDecisionEngine =
                DefaultRuntimeExperienceDecisionEngine()
        )

    @Test
    fun novel_medium_experience_is_approved_for_commit() {
        val fixture =
            fixture(
                significance =
                    RuntimeMeaningSignificance.WARNING,
                source =
                    "v1.492-approved"
            )

        val decision =
            commitDecisionEngine.decide(
                learningDecision =
                    fixture.learningDecision,
                representation =
                    fixture.representation
            )

        assertEquals(
            RuntimeAutonomousExecutionExperienceCommitDecisionState.COMMIT,
            decision.state
        )

        assertTrue(
            decision.shouldCommit
        )

        assertSame(
            fixture.representation,
            decision.representation
        )

        assertTrue(
            decision.experienceDecision.shouldRemember
        )
    }

    @Test
    fun low_importance_experience_is_not_approved_for_commit() {
        val fixture =
            fixture(
                significance =
                    RuntimeMeaningSignificance.WARNING,
                source =
                    "v1.492-low"
            )

        val lowImportanceRepresentation =
            fixture.representation.copy(
                experience =
                    fixture.representation.experience.copy(
                        importance =
                            RuntimeExperienceImportance.LOW
                    )
            )

        val decision =
            commitDecisionEngine.decide(
                learningDecision =
                    fixture.learningDecision,
                representation =
                    lowImportanceRepresentation
            )

        assertEquals(
            RuntimeAutonomousExecutionExperienceCommitDecisionState.SKIP,
            decision.state
        )

        assertFalse(
            decision.shouldCommit
        )

        assertSame(
            lowImportanceRepresentation,
            decision.representation
        )

        assertFalse(
            decision.experienceDecision.shouldRemember
        )
    }

    @Test
    fun rejected_learning_decision_must_fail_closed() {
        val fixture =
            fixture(
                significance =
                    RuntimeMeaningSignificance.WARNING,
                source =
                    "v1.492-rejected"
            )

        val rejected =
            RuntimeAutonomousExecutionExperienceLearningDecision(
                state =
                    RuntimeAutonomousExecutionExperienceLearningDecisionState.REJECTED,
                shouldProcessExperience =
                    false,
                reason =
                    "synthetic rejected decision"
            )

        val decision =
            commitDecisionEngine.decide(
                learningDecision =
                    rejected,
                representation =
                    fixture.representation
            )

        assertEquals(
            RuntimeAutonomousExecutionExperienceCommitDecisionState.REJECTED,
            decision.state
        )

        assertFalse(
            decision.shouldCommit
        )
    }

    @Test
    fun already_represented_learning_decision_must_not_commit() {
        val fixture =
            fixture(
                significance =
                    RuntimeMeaningSignificance.WARNING,
                source =
                    "v1.492-duplicate"
            )

        val duplicate =
            RuntimeAutonomousExecutionExperienceLearningDecision(
                state =
                    RuntimeAutonomousExecutionExperienceLearningDecisionState.ALREADY_REPRESENTED,
                shouldProcessExperience =
                    false,
                reason =
                    "already represented"
            )

        val decision =
            commitDecisionEngine.decide(
                learningDecision =
                    duplicate,
                representation =
                    fixture.representation
            )

        assertEquals(
            RuntimeAutonomousExecutionExperienceCommitDecisionState.SKIP,
            decision.state
        )

        assertFalse(
            decision.shouldCommit
        )
    }

    private fun fixture(
        significance: RuntimeMeaningSignificance,
        source: String
    ): Fixture {
        val composition =
            DefaultRuntimeComposition()

        val execution =
            composition
                .autonomousExecutionPipeline()
                .process(
                    intelligence =
                        RuntimeIntelligenceFixture.result(
                            significance =
                                significance,
                            confidence =
                                0.90
                        ),
                    source =
                        source,
                    authority =
                        RuntimeActionAuthorityContext(
                            source =
                                source,
                            level =
                                RuntimeAuthorityLevel.SYSTEM
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
                evaluation =
                    evaluation,
                feedback =
                    feedback
            )

        val novelty =
            DefaultRuntimeAutonomousExecutionExperienceNoveltyDeriver()
                .derive(
                    feedback
                )

        val learningDecision =
            DefaultRuntimeAutonomousExecutionExperienceLearningDecisionEngine()
                .decide(
                    analysis =
                        analysis,
                    novelty =
                        novelty
                )

        val materialization =
            DefaultRuntimeAutonomousExecutionExperienceMaterializer()
                .materialize(
                    analysis =
                        analysis,
                    novelty =
                        novelty
                )
                ?: error("fixture must materialize")

        val representation =
            DefaultRuntimeAutonomousExecutionExperienceRepresentationDeriver()
                .derive(
                    materialization
                )

        return Fixture(
            learningDecision =
                learningDecision,
            representation =
                representation
        )
    }

    private data class Fixture(
        val learningDecision:
            RuntimeAutonomousExecutionExperienceLearningDecision,
        val representation:
            pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceRepresentation
    )
}
