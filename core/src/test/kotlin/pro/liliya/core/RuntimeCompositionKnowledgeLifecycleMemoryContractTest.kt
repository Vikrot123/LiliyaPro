package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertEquals

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState

class RuntimeCompositionKnowledgeLifecycleMemoryContractTest {

    private fun knowledge(): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = "knowledge lifecycle test",
            confidence = 0.9,
            source = RuntimeKnowledgeSource.EXPERIENCE,
            createdAt = System.currentTimeMillis()
        )
    }

    @Test
    fun composition_owns_single_lifecycle_memory_instance() {
        val composition = DefaultRuntimeComposition()

        val first = composition
            .knowledgeLifecycleComposition()
            .lifecycleMemory()

        val second = composition
            .knowledgeLifecycleComposition()
            .lifecycleMemory()

        assertSame(first, second)
    }

    @Test
    fun different_compositions_do_not_share_lifecycle_memory() {
        val first = DefaultRuntimeComposition()
        val second = DefaultRuntimeComposition()

        assertNotSame(
            first.knowledgeLifecycleComposition().lifecycleMemory(),
            second.knowledgeLifecycleComposition().lifecycleMemory()
        )
    }

    @Test
    fun lifecycle_memory_creates_active_knowledge() {
        val memory = DefaultRuntimeComposition()
            .knowledgeLifecycleComposition()
            .lifecycleMemory()

        val knowledge = knowledge()

        memory.create(knowledge)

        assertEquals(
            RuntimeKnowledgeLifecycleState.ACTIVE,
            memory.memory().getLifecycleState(knowledge)
        )
    }

    @Test
    fun lifecycle_memory_revises_and_archives_knowledge() {
        val memory = DefaultRuntimeComposition()
            .knowledgeLifecycleComposition()
            .lifecycleMemory()

        val knowledge = knowledge()

        memory.create(knowledge)
        memory.revise(knowledge)

        assertEquals(
            RuntimeKnowledgeLifecycleState.REVIEW,
            memory.memory().getLifecycleState(knowledge)
        )

        memory.archive(knowledge)

        assertEquals(
            RuntimeKnowledgeLifecycleState.ARCHIVED,
            memory.memory().getLifecycleState(knowledge)
        )
    }
    @Test
    fun composition_reset_preserves_knowledge_lifecycle_state() {
        val composition = DefaultRuntimeComposition()
        val lifecycle = composition.knowledgeLifecycleComposition()
        val knowledge = knowledge()

        lifecycle.lifecycleMemory().create(knowledge)

        assertEquals(
            RuntimeKnowledgeLifecycleState.ACTIVE,
            lifecycle.lifecycleMemory()
                .memory()
                .getLifecycleState(knowledge)
        )

        lifecycle.reset()

        assertEquals(
            RuntimeKnowledgeLifecycleState.ACTIVE,
            lifecycle.lifecycleMemory()
                .memory()
                .getLifecycleState(knowledge)
        )
    }

    @Test
    fun composition_preserves_shared_knowledge_memory_identity() {
        val composition = DefaultRuntimeComposition()

        val memoryComposition =
            composition.memoryComposition()

        val lifecycleMemory =
            composition
                .knowledgeLifecycleComposition()
                .lifecycleMemory()

        assertSame(
            memoryComposition.knowledgeMemory(),
            lifecycleMemory.memory()
        )
    }

    @Test
    fun prepare_preserves_shared_knowledge_memory_identity() {
        val composition = DefaultRuntimeComposition()

        val before =
            composition
                .memoryComposition()
                .knowledgeMemory()

        composition.prepareRuntime()

        val after =
            composition
                .knowledgeLifecycleComposition()
                .lifecycleMemory()
                .memory()

        assertSame(before, after)
    }

    @Test
    fun composition_preserves_knowledge_lifecycle_state_store_identity() {
        val composition = DefaultRuntimeComposition()

        val memoryStore = composition
            .memoryComposition()
            .knowledgeLifecycleStateStore()

        val lifecycleMemory = composition
            .knowledgeLifecycleComposition()
            .lifecycleMemory()

        val state = RuntimeKnowledge(
            statement = "state store identity",
            confidence = 0.9,
            source = RuntimeKnowledgeSource.EXPERIENCE,
            createdAt = 1L
        )

        lifecycleMemory.create(state)

        assertSame(
            RuntimeKnowledgeLifecycleState.ACTIVE,
            memoryStore.getState(state)
        )
    }

    @Test
    fun prepare_preserves_knowledge_lifecycle_state_store_identity() {
        val composition = DefaultRuntimeComposition()

        val before = composition
            .memoryComposition()
            .knowledgeLifecycleStateStore()

        composition.prepareRuntime()

        val after = composition
            .memoryComposition()
            .knowledgeLifecycleStateStore()

        assertSame(before, after)
    }

    @Test
    fun reset_preserves_shared_state_and_history_wiring() {
        val composition = DefaultRuntimeComposition()
        val lifecycle = composition.knowledgeLifecycleComposition()

        val knowledge = RuntimeKnowledge(
            statement = "reset shared state wiring",
            confidence = 0.9,
            source = RuntimeKnowledgeSource.EXPERIENCE,
            createdAt = 1L
        )

        val memory = lifecycle.lifecycleMemory()
        val history = lifecycle.lifecycleHistoryQuery()
        val stateStore = composition
            .memoryComposition()
            .knowledgeLifecycleStateStore()

        memory.create(knowledge)

        assertSame(
            RuntimeKnowledgeLifecycleState.ACTIVE,
            stateStore.getState(knowledge)
        )

        assertEquals(
            1,
            history.transitionCount(knowledge)
        )

        lifecycle.reset()

        val afterMemory = lifecycle.lifecycleMemory()
        val afterHistory = lifecycle.lifecycleHistoryQuery()

        assertSame(memory, afterMemory)
        assertSame(history, afterHistory)

        assertSame(
            RuntimeKnowledgeLifecycleState.ACTIVE,
            stateStore.getState(knowledge)
        )

        afterMemory.revise(knowledge)

        assertSame(
            RuntimeKnowledgeLifecycleState.REVIEW,
            stateStore.getState(knowledge)
        )

        assertEquals(
            2,
            afterHistory.transitionCount(knowledge)
        )
    }

}
