package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState

class RuntimeCompositionKnowledgeMaintenanceEndToEndContractTest {

    @Test
    fun runtime_composition_can_self_maintain_knowledge_memory() {
        val composition =
            DefaultRuntimeComposition()

        val lifecycle =
            composition
                .knowledgeLifecycleComposition()

        val memory =
            lifecycle.lifecycleMemory()

        val weak =
            RuntimeKnowledge(
                statement =
                    "runtime weak maintenance knowledge",
                confidence = 0.30,
                source =
                    RuntimeKnowledgeSource.CONSOLIDATION,
                createdAt = 1L
            )

        memory.create(weak)

        val first =
            lifecycle
                .maintenanceService()
                .maintain()

        assertEquals(
            1,
            first.reviewedCount
        )

        assertEquals(
            RuntimeKnowledgeLifecycleState.REVIEW,
            memory
                .memory()
                .getLifecycleState(weak)
        )

        val second =
            lifecycle
                .maintenanceService()
                .maintain()

        assertEquals(
            1,
            second.archivedCount
        )

        assertEquals(
            RuntimeKnowledgeLifecycleState.ARCHIVED,
            memory
                .memory()
                .getLifecycleState(weak)
        )

        assertTrue(
            memory
                .memory()
                .retrieveRelevant(
                    weak.statement
                )
                .isEmpty()
        )
    }
}
