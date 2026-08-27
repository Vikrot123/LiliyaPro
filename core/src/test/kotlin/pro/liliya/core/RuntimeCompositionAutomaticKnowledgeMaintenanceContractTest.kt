package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionAutomaticKnowledgeMaintenanceContractTest {

    @Test
    fun knowledge_producing_intelligence_cycle_runs_maintenance_automatically() {
        val composition =
            DefaultRuntimeComposition()

        val result =
            composition
                .intelligenceOrchestrator()
                .process()

        assertNotNull(
            result
                .experienceKnowledge
                .pipelineResult
                .knowledgeResult,
            "test precondition: intelligence cycle must produce knowledge"
        )

        val maintenance =
            assertNotNull(
                result.knowledgeMaintenance
            )

        assertTrue(
            maintenance.processedCount > 0
        )
    }

    @Test
    fun automatic_maintenance_preserves_semantic_knowledge_reuse() {
        val composition =
            DefaultRuntimeComposition()

        val result =
            composition
                .intelligenceOrchestrator()
                .process()

        assertNotNull(
            result.knowledgeMaintenance
        )

        val memory =
            composition
                .knowledgeLifecycleComposition()
                .lifecycleMemory()
                .memory()

        val available =
            memory.availableKnowledge()

        assertTrue(
            available.isNotEmpty()
        )

        val knowledge =
            available.last()

        assertTrue(
            memory
                .retrieveRelevant(
                    knowledge.statement
                )
                .any {
                    it.knowledge == knowledge
                }
        )
    }
}
