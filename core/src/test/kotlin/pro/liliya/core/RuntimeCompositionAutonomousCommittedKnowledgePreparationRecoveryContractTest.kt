package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceCommitState

class RuntimeCompositionAutonomousCommittedKnowledgePreparationRecoveryContractTest {

    @Test
    fun autonomous_committed_knowledge_survives_prepare_recovery_boundary() {
        val composition =
            DefaultRuntimeComposition()

        val lifecycleMemory =
            composition
                .knowledgeLifecycleComposition()
                .lifecycleMemory()

        val sharedMemory =
            lifecycleMemory.memory()

        val firstIntelligence =
            RuntimeIntelligenceFixture.result(
                significance =
                    RuntimeMeaningSignificance.WARNING,
                confidence = 0.90
            )

        val firstExecution =
            composition
                .autonomousExecutionPipeline()
                .process(
                    intelligence = firstIntelligence,
                    source = "v1.515-before-recovery",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "v1.515-before-recovery",
                            level = RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val firstEvaluation =
            composition
                .autonomousExecutionEvaluationPipeline()
                .evaluate(firstExecution)

        val first =
            composition
                .autonomousPostExecutionLearningPipeline()
                .process(firstEvaluation)

        val firstCommit =
            assertNotNull(
                first.commitResult,
                "first autonomous execution must reach commit boundary"
            )

        assertEquals(
            RuntimeAutonomousExecutionExperienceCommitState.COMMITTED,
            firstCommit.state
        )

        val firstKnowledgeBridge =
            assertNotNull(
                first.committedExperienceKnowledge,
                "first committed experience must enter knowledge pipeline"
            )

        assertSame(
            firstCommit.experience,
            firstKnowledgeBridge.experience
        )

        val firstKnowledge =
            firstKnowledgeBridge
                .knowledgeResult
                .knowledge

        assertTrue(
            sharedMemory
                .query(firstKnowledge.statement)
                .any { ranked ->
                    ranked.result.node.knowledge === firstKnowledge
                },
            "knowledge created before recovery boundary must be retrievable"
        )

        val memoryBeforeRecovery =
            composition
                .knowledgeLifecycleComposition()
                .lifecycleMemory()

        composition.prepareRuntime()

        val memoryAfterRecovery =
            composition
                .knowledgeLifecycleComposition()
                .lifecycleMemory()

        assertSame(
            memoryBeforeRecovery,
            memoryAfterRecovery,
            "prepare recovery boundary must preserve knowledge lifecycle memory owner"
        )

        assertTrue(
            memoryAfterRecovery
                .memory()
                .query(firstKnowledge.statement)
                .any { ranked ->
                    ranked.result.node.knowledge === firstKnowledge
                },
            "autonomous knowledge created before recovery must survive prepareRuntime"
        )

        val recoveredIntelligence =
            RuntimeIntelligenceFixture.result(
                significance =
                    RuntimeMeaningSignificance.WARNING,
                confidence = 0.91
            )

        val recoveredExecution =
            composition
                .autonomousExecutionPipeline()
                .process(
                    intelligence = recoveredIntelligence,
                    source = "v1.515-after-recovery",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "v1.515-after-recovery",
                            level = RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val recoveredEvaluation =
            composition
                .autonomousExecutionEvaluationPipeline()
                .evaluate(recoveredExecution)

        val recovered =
            composition
                .autonomousPostExecutionLearningPipeline()
                .process(recoveredEvaluation)

        val recoveredCommit =
            assertNotNull(
                recovered.commitResult,
                "recovered autonomous execution must reach commit boundary"
            )

        assertEquals(
            RuntimeAutonomousExecutionExperienceCommitState.COMMITTED,
            recoveredCommit.state
        )

        val recoveredKnowledgeBridge =
            assertNotNull(
                recovered.committedExperienceKnowledge,
                "recovered committed experience must enter knowledge pipeline"
            )

        assertSame(
            recoveredCommit.experience,
            recoveredKnowledgeBridge.experience
        )

        val recoveredKnowledge =
            recoveredKnowledgeBridge
                .knowledgeResult
                .knowledge

        assertNotSame(
            firstCommit.experience,
            recoveredCommit.experience,
            "recovered execution must produce independent experience"
        )

        assertNotSame(
            firstKnowledge,
            recoveredKnowledge,
            "recovered execution must produce independent knowledge"
        )

        val sharedMemoryAfterRecovery =
            memoryAfterRecovery.memory()

        assertTrue(
            sharedMemoryAfterRecovery
                .query(firstKnowledge.statement)
                .any { ranked ->
                    ranked.result.node.knowledge === firstKnowledge
                },
            "pre-recovery autonomous knowledge must remain available"
        )

        assertTrue(
            sharedMemoryAfterRecovery
                .query(recoveredKnowledge.statement)
                .any { ranked ->
                    ranked.result.node.knowledge === recoveredKnowledge
                },
            "post-recovery autonomous knowledge must be added independently"
        )
    }
}
