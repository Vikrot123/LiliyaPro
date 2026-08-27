package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNull
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.integration.DefaultRuntimeKnowledgeMemory
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.integration.DefaultRuntimeKnowledgeLifecycleMemory
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state.DefaultRuntimeKnowledgeLifecycleStateStore

class RuntimeKnowledgeLifecycleMemoryHygieneContractTest {

    @Test
    fun duplicate_create_does_not_multiply_retrievable_knowledge() {
        val stateStore =
            DefaultRuntimeKnowledgeLifecycleStateStore()

        val memory =
            DefaultRuntimeKnowledgeMemory(
                lifecycleStateStore = stateStore
            )

        val lifecycle =
            DefaultRuntimeKnowledgeLifecycleMemory(
                stateStore = stateStore,
                knowledgeMemory = memory
            )

        val first =
            knowledge(
                "runtime stable operational knowledge",
                0.8,
                1L
            )

        val duplicate =
            knowledge(
                "Runtime stable operational knowledge.",
                0.8,
                2L
            )

        lifecycle.create(first)
        lifecycle.create(duplicate)

        val available =
            memory.availableKnowledge()

        assertEquals(
            1,
            available.size
        )

        assertEquals(
            first,
            available.single()
        )

        assertTrue(
            memory
                .retrieveRelevant(
                    "runtime stable operational knowledge"
                )
                .all {
                    it.knowledge == first
                }
        )
    }

    @Test
    fun suppressed_duplicate_does_not_receive_independent_lifecycle_state() {
        val stateStore =
            DefaultRuntimeKnowledgeLifecycleStateStore()

        val memory =
            DefaultRuntimeKnowledgeMemory(
                lifecycleStateStore = stateStore
            )

        val lifecycle =
            DefaultRuntimeKnowledgeLifecycleMemory(
                stateStore = stateStore,
                knowledgeMemory = memory
            )

        val first =
            knowledge(
                "runtime lifecycle ownership knowledge",
                0.8,
                1L
            )

        val duplicate =
            knowledge(
                "Runtime lifecycle ownership knowledge.",
                0.8,
                2L
            )

        lifecycle.create(first)
        lifecycle.create(duplicate)

        assertEquals(
            1,
            memory.availableKnowledge().size
        )

        assertEquals(
            RuntimeKnowledgeLifecycleState.ACTIVE,
            memory.getLifecycleState(first)
        )

        assertNull(
            memory.getLifecycleState(duplicate),
            "suppressed duplicate must not create ghost lifecycle state"
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
