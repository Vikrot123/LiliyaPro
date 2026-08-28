package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
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

class RuntimeCommittedExperienceKnowledgePipelineContractTest {

    private fun context(): RuntimeExperienceContext {
        return RuntimeExperienceContext(
            selfModel =
                RuntimeSelfModel(
                    snapshot =
                        RuntimeContextSnapshot(
                            runtimeState = "RUNNING",
                            activeServices = listOf("runtime"),
                            timestamp = 1L
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
                    interpretation =
                        "v1.513 committed experience knowledge bridge",
                    confidence = 1.0,
                    significance =
                        RuntimeMeaningSignificance.WARNING,
                    generatedAt = 1L
                )
        )
    }

    @Test
    fun committed_experience_can_enter_knowledge_without_second_experience_commit() {
        val composition =
            DefaultRuntimeComposition()

        val experienceStore =
            composition
                .experienceComposition()
                .experienceStore()

        val experienceResult =
            composition
                .experienceComposition()
                .experiencePipeline()
                .process(context())

        if (!experienceResult.decision.shouldRemember) {
            error(
                "test precondition failed: source experience must be remembered"
            )
        }

        val committedExperience =
            experienceResult.experience

        val beforeBridge =
            experienceStore.experiences().size

        assertTrue(
            experienceStore.experiences().any {
                it === committedExperience
            },
            "source experience must already be committed before knowledge bridge"
        )

        val bridge =
            composition.committedExperienceKnowledgePipeline()

        assertSame(
            bridge,
            composition.committedExperienceKnowledgePipeline(),
            "composition must own stable committed-experience knowledge pipeline"
        )

        val result =
            bridge.process(committedExperience)

        assertSame(
            committedExperience,
            result.experience,
            "knowledge bridge must preserve committed experience identity"
        )

        assertNotNull(
            result.consolidation
        )

        val knowledge =
            assertNotNull(
                result.knowledgeResult.knowledge
            )

        assertEquals(
            beforeBridge,
            experienceStore.experiences().size,
            "knowledge bridge must not append a second experience"
        )

        assertTrue(
            experienceStore.experiences().any {
                it === committedExperience
            },
            "knowledge bridge must leave original committed experience intact"
        )

        val memory =
            composition
                .knowledgeLifecycleComposition()
                .lifecycleMemory()
                .memory()

        assertEquals(
            RuntimeKnowledgeLifecycleState.ACTIVE,
            memory.getLifecycleState(knowledge),
            "knowledge derived from committed experience must enter lifecycle memory"
        )

        assertTrue(
            memory
                .query(knowledge.statement)
                .any { ranked ->
                    ranked.result.node.knowledge === knowledge
                },
            "knowledge derived from committed experience must be retrievable"
        )
    }
}
