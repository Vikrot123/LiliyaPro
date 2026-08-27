package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.composition.DefaultRuntimeKnowledgeLifecycleComposition
import pro.liliya.core.runtime.intelligence.knowledge.integration.DefaultRuntimeKnowledgeMemory
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state.DefaultRuntimeKnowledgeLifecycleStateStore

class RuntimeKnowledgeMaintenanceServiceContractTest {

    @Test
    fun composition_owns_stable_maintenance_service() {
        val composition =
            composition()

        assertSame(
            composition.maintenanceService(),
            composition.maintenanceService()
        )
    }

    @Test
    fun maintenance_reports_keep_and_review_decisions() {
        val composition =
            composition()

        val memory =
            composition.lifecycleMemory()

        val strong =
            knowledge(
                "runtime stable operational knowledge",
                0.80,
                1L
            )

        val weak =
            knowledge(
                "runtime uncertain operational knowledge",
                0.30,
                2L
            )

        memory.create(strong)
        memory.create(weak)

        val report =
            composition
                .maintenanceService()
                .maintain()

        assertEquals(2, report.processedCount)
        assertEquals(1, report.keptCount)
        assertEquals(1, report.reviewedCount)
        assertEquals(0, report.archivedCount)

        assertEquals(
            RuntimeKnowledgeLifecycleState.ACTIVE,
            memory
                .memory()
                .getLifecycleState(strong)
        )

        assertEquals(
            RuntimeKnowledgeLifecycleState.REVIEW,
            memory
                .memory()
                .getLifecycleState(weak)
        )
    }

    @Test
    fun repeated_maintenance_archives_persistently_weak_knowledge() {
        val composition =
            composition()

        val memory =
            composition.lifecycleMemory()

        val weak =
            knowledge(
                "runtime weak operational knowledge",
                0.30,
                1L
            )

        memory.create(weak)

        val first =
            composition
                .maintenanceService()
                .maintain()

        assertEquals(1, first.reviewedCount)

        assertEquals(
            RuntimeKnowledgeLifecycleState.REVIEW,
            memory
                .memory()
                .getLifecycleState(weak)
        )

        assertTrue(
            memory
                .memory()
                .retrieveRelevant(
                    "runtime weak operational knowledge"
                )
                .any {
                    it.knowledge == weak
                }
        )

        val second =
            composition
                .maintenanceService()
                .maintain()

        assertEquals(1, second.archivedCount)

        assertEquals(
            RuntimeKnowledgeLifecycleState.ARCHIVED,
            memory
                .memory()
                .getLifecycleState(weak)
        )

        assertEquals(
            emptyList(),
            memory
                .memory()
                .retrieveRelevant(
                    "runtime weak operational knowledge"
                )
        )
    }

    private fun composition():
        DefaultRuntimeKnowledgeLifecycleComposition {

        val stateStore =
            DefaultRuntimeKnowledgeLifecycleStateStore()

        val memory =
            DefaultRuntimeKnowledgeMemory(
                lifecycleStateStore = stateStore
            )

        return DefaultRuntimeKnowledgeLifecycleComposition(
            memory,
            stateStore
        )
    }

    private fun knowledge(
        statement: String,
        confidence: Double,
        createdAt: Long
    ) =
        RuntimeKnowledge(
            statement = statement,
            confidence = confidence,
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = createdAt
        )
}
