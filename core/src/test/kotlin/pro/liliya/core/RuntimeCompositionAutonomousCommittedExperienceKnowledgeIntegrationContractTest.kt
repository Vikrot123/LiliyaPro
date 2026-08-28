package pro.liliya.core

import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel

import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceCommitState
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeCompositionAutonomousCommittedExperienceKnowledgeIntegrationContractTest {

    @Test
    fun committed_autonomous_experience_enters_knowledge_with_same_identity() {
        val composition =
            DefaultRuntimeComposition()

        val experienceStore =
            composition
                .experienceComposition()
                .experienceStore()

        val before =
            experienceStore.experiences().size

        val intelligence =
            RuntimeIntelligenceFixture.result(
                significance =
                    RuntimeMeaningSignificance.WARNING,
                confidence = 0.90
            )

        val execution =
            composition
                .autonomousExecutionPipeline()
                .process(
                    intelligence = intelligence,
                    source = "v1.514-autonomous-knowledge",
                    authority =
                        RuntimeActionAuthorityContext(
                            source =
                                "v1.514-autonomous-knowledge",
                            level =
                                RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val evaluation =
            composition
                .autonomousExecutionEvaluationPipeline()
                .evaluate(execution)

        val result =
            composition
                .autonomousPostExecutionLearningPipeline()
                .process(evaluation)

        val commitResult =
            assertNotNull(
                result.commitResult,
                "test requires autonomous experience commit result"
            )

        assertEquals(
            RuntimeAutonomousExecutionExperienceCommitState.COMMITTED,
            commitResult.state,
            "test requires a newly committed autonomous experience"
        )

        assertEquals(
            before + 1,
            experienceStore.experiences().size,
            "autonomous experience must be committed exactly once"
        )

        assertTrue(
            experienceStore.experiences().any {
                it === commitResult.experience
            },
            "committed autonomous experience must remain in shared experience store"
        )

        val knowledgeBridgeResult =
            assertNotNull(
                result.committedExperienceKnowledge,
                "new autonomous commit must enter committed-experience knowledge pipeline"
            )

        assertSame(
            commitResult.experience,
            knowledgeBridgeResult.experience,
            "knowledge integration must preserve autonomous committed experience identity"
        )

        assertEquals(
            before + 1,
            experienceStore.experiences().size,
            "knowledge integration must not append a second experience"
        )

        val knowledge =
            knowledgeBridgeResult
                .knowledgeResult
                .knowledge

        val memory =
            composition
                .knowledgeLifecycleComposition()
                .lifecycleMemory()
                .memory()

        assertTrue(
            memory
                .query(knowledge.statement)
                .any { ranked ->
                    ranked.result.node.knowledge === knowledge
                },
            "knowledge derived from autonomous committed experience must be retrievable"
        )
    }

    @Test
    fun already_committed_autonomous_experience_does_not_enter_knowledge_twice() {
        val composition =
            DefaultRuntimeComposition()

        val experienceStore =
            composition
                .experienceComposition()
                .experienceStore()

        val intelligence =
            RuntimeIntelligenceFixture.result(
                significance =
                    RuntimeMeaningSignificance.WARNING,
                confidence = 0.90
            )

        val execution =
            composition
                .autonomousExecutionPipeline()
                .process(
                    intelligence = intelligence,
                    source = "v1.514-autonomous-idempotency",
                    authority =
                        RuntimeActionAuthorityContext(
                            source =
                                "v1.514-autonomous-idempotency",
                            level =
                                RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val evaluation =
            composition
                .autonomousExecutionEvaluationPipeline()
                .evaluate(execution)

        val pipeline =
            composition
                .autonomousPostExecutionLearningPipeline()

        val before =
            experienceStore.experiences().size

        val first =
            pipeline.process(evaluation)

        val firstCommit =
            assertNotNull(
                first.commitResult,
                "first processing must reach commit boundary"
            )

        assertEquals(
            RuntimeAutonomousExecutionExperienceCommitState.COMMITTED,
            firstCommit.state
        )

        val firstKnowledge =
            assertNotNull(
                first.committedExperienceKnowledge,
                "new commit must enter knowledge pipeline"
            )

        assertSame(
            firstCommit.experience,
            firstKnowledge.experience
        )

        val afterFirstCommit =
            experienceStore.experiences().size

        assertEquals(
            before + 1,
            afterFirstCommit,
            "first processing must append exactly one experience"
        )

        val second =
            pipeline.process(evaluation)

        val secondCommit =
            assertNotNull(
                second.commitResult,
                "reprocessing must still reach commit boundary"
            )

        assertEquals(
            RuntimeAutonomousExecutionExperienceCommitState.ALREADY_COMMITTED,
            secondCommit.state,
            "same execution identity must remain idempotent"
        )

        assertTrue(
            experienceStore.experiences().any {
                it === firstCommit.experience
            },
            "original committed experience must remain in shared store"
        )

        assertEquals(
            null,
            second.committedExperienceKnowledge,
            "ALREADY_COMMITTED must not invoke knowledge pipeline again"
        )

        assertEquals(
            afterFirstCommit,
            experienceStore.experiences().size,
            "ALREADY_COMMITTED must not append another experience"
        )
    }

}
