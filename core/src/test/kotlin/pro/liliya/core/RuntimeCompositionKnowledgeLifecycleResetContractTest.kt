package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertSame
import kotlin.test.assertEquals

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState

class RuntimeCompositionKnowledgeLifecycleResetContractTest {

    private fun knowledge(): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = "reset lifecycle test",
            confidence = 0.9,
            source = RuntimeKnowledgeSource.EXPERIENCE,
            createdAt = System.currentTimeMillis()
        )
    }

    @Test
    fun reset_keeps_knowledge_lifecycle_memory_owner() {
        val composition = DefaultRuntimeComposition()

        val before =
            composition
                .knowledgeLifecycleComposition()
                .lifecycleMemory()

        composition
            .knowledgeLifecycleComposition()
            .reset()

        val after =
            composition
                .knowledgeLifecycleComposition()
                .lifecycleMemory()

        assertSame(before, after)
    }

    @Test
    fun reset_keeps_existing_history() {
        val composition = DefaultRuntimeComposition()

        val knowledge = knowledge()

        val memory =
            composition
                .knowledgeLifecycleComposition()
                .lifecycleMemory()

        memory.create(knowledge)
        memory.revise(knowledge)

        composition
            .knowledgeLifecycleComposition()
            .reset()

        val count =
            composition
                .knowledgeLifecycleComposition()
                .lifecycleHistoryQuery()
                .transitionCount(knowledge)

        assertEquals(2, count)
    }
}
