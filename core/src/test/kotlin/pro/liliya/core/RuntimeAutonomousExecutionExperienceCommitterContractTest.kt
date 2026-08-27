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
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.DefaultRuntimeAutonomousExecutionExperienceCommitter
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.DefaultRuntimeAutonomousExecutionExperienceLearningDecisionEngine
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.DefaultRuntimeAutonomousExecutionExperienceMaterializer
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.DefaultRuntimeAutonomousExecutionExperienceNoveltyDeriver
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.DefaultRuntimeAutonomousExecutionExperienceRepresentationDeriver
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceCommitDecision
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceCommitDecisionState
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceCommitState
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection.DefaultRuntimeAutonomousExecutionReflectionAnalyzer
import pro.liliya.core.runtime.intelligence.experience.decision.DefaultRuntimeExperienceDecisionEngine
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeAutonomousExecutionExperienceCommitterContractTest {

    @Test
    fun approved_commit_records_exact_representation_experience_once() {
        val fixture =
            fixture(
                source = "v1.493-commit"
            )

        val store =
            fixture.composition
                .experienceComposition()
                .experienceStore()

        val committer =
            DefaultRuntimeAutonomousExecutionExperienceCommitter(
                experienceStore = store
            )

        val before =
            store.experiences().size

        val result =
            committer.commit(
                fixture.commitDecision
            )

        val records =
            store.experiences()

        assertEquals(
            RuntimeAutonomousExecutionExperienceCommitState.COMMITTED,
            result.state
        )

        assertTrue(
            result.committed
        )

        assertSame(
            fixture.commitDecision.representation.experience,
            result.experience
        )

        assertEquals(
            before + 1,
            records.size
        )

        assertSame(
            fixture.commitDecision.representation.experience,
            records.last()
        )
    }

    @Test
    fun repeated_commit_of_same_representation_is_idempotent() {
        val fixture =
            fixture(
                source = "v1.493-idempotent"
            )

        val store =
            fixture.composition
                .experienceComposition()
                .experienceStore()

        val committer =
            DefaultRuntimeAutonomousExecutionExperienceCommitter(
                experienceStore = store
            )

        val before =
            store.experiences().size

        val first =
            committer.commit(
                fixture.commitDecision
            )

        val second =
            committer.commit(
                fixture.commitDecision
            )

        val after =
            store.experiences().size

        assertEquals(
            RuntimeAutonomousExecutionExperienceCommitState.COMMITTED,
            first.state
        )

        assertEquals(
            RuntimeAutonomousExecutionExperienceCommitState.ALREADY_COMMITTED,
            second.state
        )

        assertFalse(
            second.committed
        )

        assertEquals(
            before + 1,
            after,
            "the same autonomous representation must not be appended twice"
        )

        assertSame(
            first.experience,
            second.experience
        )
    }

    @Test
    fun non_commit_decision_never_mutates_experience_store() {
        val fixture =
            fixture(
                source = "v1.493-skip"
            )

        val store =
            fixture.composition
                .experienceComposition()
                .experienceStore()

        val committer =
            DefaultRuntimeAutonomousExecutionExperienceCommitter(
                experienceStore = store
            )

        val skipDecision =
            RuntimeAutonomousExecutionExperienceCommitDecision(
                state =
                    RuntimeAutonomousExecutionExperienceCommitDecisionState.SKIP,
                shouldCommit =
                    false,
                representation =
                    fixture.commitDecision.representation,
                experienceDecision =
                    fixture.commitDecision.experienceDecision,
                reason =
                    "synthetic skip"
            )

        val before =
            store.experiences().size

        val result =
            committer.commit(
                skipDecision
            )

        assertEquals(
            RuntimeAutonomousExecutionExperienceCommitState.SKIPPED,
            result.state
        )

        assertFalse(
            result.committed
        )

        assertEquals(
            before,
            store.experiences().size
        )
    }

    @Test
    fun rejected_commit_decision_fails_closed_without_store_mutation() {
        val fixture =
            fixture(
                source = "v1.493-rejected"
            )

        val store =
            fixture.composition
                .experienceComposition()
                .experienceStore()

        val committer =
            DefaultRuntimeAutonomousExecutionExperienceCommitter(
                experienceStore = store
            )

        val rejected =
            RuntimeAutonomousExecutionExperienceCommitDecision(
                state =
                    RuntimeAutonomousExecutionExperienceCommitDecisionState.REJECTED,
                shouldCommit =
                    false,
                representation =
                    fixture.commitDecision.representation,
                experienceDecision =
                    fixture.commitDecision.experienceDecision,
                reason =
                    "synthetic rejection"
            )

        val before =
            store.experiences().size

        val result =
            committer.commit(
                rejected
            )

        assertEquals(
            RuntimeAutonomousExecutionExperienceCommitState.REJECTED,
            result.state
        )

        assertFalse(
            result.committed
        )

        assertEquals(
            before,
            store.experiences().size
        )
    }

    private fun fixture(
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
                                RuntimeMeaningSignificance.WARNING,
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

        val commitDecision =
            DefaultRuntimeAutonomousExecutionExperienceCommitDecisionEngine(
                experienceDecisionEngine =
                    DefaultRuntimeExperienceDecisionEngine()
            ).decide(
                learningDecision =
                    learningDecision,
                representation =
                    representation
            )

        return Fixture(
            composition =
                composition,
            commitDecision =
                commitDecision
        )
    }

    private data class Fixture(
        val composition: DefaultRuntimeComposition,
        val commitDecision:
            RuntimeAutonomousExecutionExperienceCommitDecision
    )
}
