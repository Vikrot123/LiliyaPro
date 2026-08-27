package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.context.RuntimeContextMetadata
import pro.liliya.core.runtime.intelligence.context.RuntimeContextSnapshot
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceContext
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.selfmodel.RuntimeSelfModel

class RuntimeCompositionExperienceKnowledgeFailureRecoveryContinuityContractTest {

    private fun context(
        interpretation: String,
        timestamp: Long
    ): RuntimeExperienceContext {
        return RuntimeExperienceContext(
            selfModel =
                RuntimeSelfModel(
                    snapshot =
                        RuntimeContextSnapshot(
                            runtimeState = "RUNNING",
                            activeServices = listOf("runtime"),
                            timestamp = timestamp
                        ),
                    metadata =
                        RuntimeContextMetadata(
                            runtimeVersion = "1",
                            recoveryAvailable = true,
                            diagnosticsAvailable = true
                        )
                ),
            meaning =
                RuntimeMeaningResult(
                    interpretation = interpretation,
                    confidence = 1.0,
                    significance =
                        RuntimeMeaningSignificance.WARNING,
                    generatedAt = timestamp
                )
        )
    }

    @Test
    fun experience_derived_knowledge_survives_failure_recovery_and_new_knowledge_remains_independent() {
        val composition =
            DefaultRuntimeComposition()

        val pipelineBefore =
            composition.experienceKnowledgePipeline()

        val experienceStore =
            composition
                .experienceComposition()
                .experienceStore()

        val lifecycleBefore =
            composition.knowledgeLifecycleComposition()

        val lifecycleMemoryBefore =
            lifecycleBefore.lifecycleMemory()

        val sharedMemoryBefore =
            lifecycleMemoryBefore.memory()

        val experiencesBefore =
            experienceStore.experiences().size

        val firstResult =
            pipelineBefore.process(
                context(
                    interpretation =
                        "v1.512 knowledge before failure",
                    timestamp = 1L
                )
            )

        if (!firstResult.experienceResult.decision.shouldRemember) {
            error(
                "test precondition failed: pre-failure experience must be remembered"
            )
        }

        val firstKnowledge =
            assertNotNull(
                firstResult.knowledgeResult?.knowledge
            )

        val firstExperience =
            firstResult.experienceResult.experience

        assertNotNull(
            firstResult.consolidation
        )

        assertEquals(
            experiencesBefore + 1,
            experienceStore.experiences().size,
            "pre-failure pipeline must commit exactly one experience"
        )

        assertTrue(
            experienceStore.experiences().any {
                it === firstExperience
            },
            "pre-failure experience must be present by identity"
        )

        assertEquals(
            RuntimeKnowledgeLifecycleState.ACTIVE,
            sharedMemoryBefore.getLifecycleState(firstKnowledge),
            "pre-failure experience-derived knowledge must enter lifecycle memory"
        )

        assertTrue(
            sharedMemoryBefore
                .query(firstKnowledge.statement)
                .any { ranked ->
                    ranked.result.node.knowledge === firstKnowledge
                },
            "pre-failure knowledge must be retrievable before failure"
        )

        composition.markRuntimeFailed(
            "v1.512-failure-boundary"
        )

        assertEquals(
            CoreRuntimeState.FAILED,
            composition.runtimeState()
        )

        composition.prepareRuntime()

        val pipelineAfter =
            composition.experienceKnowledgePipeline()

        val lifecycleAfter =
            composition.knowledgeLifecycleComposition()

        val lifecycleMemoryAfter =
            lifecycleAfter.lifecycleMemory()

        val sharedMemoryAfter =
            lifecycleMemoryAfter.memory()

        assertSame(
            pipelineBefore,
            pipelineAfter,
            "failure recovery reset must preserve root experience-knowledge pipeline owner"
        )

        assertSame(
            lifecycleBefore,
            lifecycleAfter,
            "failure recovery reset must preserve knowledge lifecycle composition owner"
        )

        assertSame(
            lifecycleMemoryBefore,
            lifecycleMemoryAfter,
            "failure recovery reset must preserve lifecycle memory owner"
        )

        assertSame(
            sharedMemoryBefore,
            sharedMemoryAfter,
            "failure recovery reset must preserve shared knowledge memory owner"
        )

        assertEquals(
            RuntimeKnowledgeLifecycleState.ACTIVE,
            sharedMemoryAfter.getLifecycleState(firstKnowledge),
            "pre-failure knowledge lifecycle state must survive recovery reset"
        )

        assertTrue(
            sharedMemoryAfter
                .query(firstKnowledge.statement)
                .any { ranked ->
                    ranked.result.node.knowledge === firstKnowledge
                },
            "pre-failure experience-derived knowledge must remain retrievable after recovery reset"
        )

        assertEquals(
            experiencesBefore + 1,
            experienceStore.experiences().size,
            "failure recovery reset must not duplicate or discard committed experience"
        )

        val recoveredResult =
            pipelineAfter.process(
                context(
                    interpretation =
                        "v1.512 knowledge after recovery",
                    timestamp = 2L
                )
            )

        if (!recoveredResult.experienceResult.decision.shouldRemember) {
            error(
                "test precondition failed: recovered experience must be remembered"
            )
        }

        val recoveredKnowledge =
            assertNotNull(
                recoveredResult.knowledgeResult?.knowledge
            )

        val recoveredExperience =
            recoveredResult.experienceResult.experience

        assertNotNull(
            recoveredResult.consolidation
        )

        assertNotSame(
            firstExperience,
            recoveredExperience,
            "recovered experience must have independent identity"
        )

        assertNotSame(
            firstKnowledge,
            recoveredKnowledge,
            "recovered knowledge must have independent identity"
        )

        assertEquals(
            experiencesBefore + 2,
            experienceStore.experiences().size,
            "recovered pipeline must append exactly one independent experience"
        )

        assertTrue(
            experienceStore.experiences().any {
                it === firstExperience
            },
            "pre-failure experience must remain stored after recovered processing"
        )

        assertTrue(
            experienceStore.experiences().any {
                it === recoveredExperience
            },
            "recovered experience must be stored independently"
        )

        assertEquals(
            RuntimeKnowledgeLifecycleState.ACTIVE,
            sharedMemoryAfter.getLifecycleState(firstKnowledge),
            "recovered processing must not disturb pre-failure knowledge"
        )

        assertEquals(
            RuntimeKnowledgeLifecycleState.ACTIVE,
            sharedMemoryAfter.getLifecycleState(recoveredKnowledge),
            "recovered experience-derived knowledge must enter lifecycle memory"
        )

        assertTrue(
            sharedMemoryAfter
                .query(firstKnowledge.statement)
                .any { ranked ->
                    ranked.result.node.knowledge === firstKnowledge
                },
            "pre-failure knowledge must remain retrievable after recovered processing"
        )

        assertTrue(
            sharedMemoryAfter
                .query(recoveredKnowledge.statement)
                .any { ranked ->
                    ranked.result.node.knowledge === recoveredKnowledge
                },
            "recovered knowledge must be independently retrievable"
        )
    }
}
