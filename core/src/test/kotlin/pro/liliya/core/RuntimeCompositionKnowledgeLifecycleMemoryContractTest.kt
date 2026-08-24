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
}
