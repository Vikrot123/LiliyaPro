package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState

class RuntimeCompositionKnowledgeLifecycleFlowContractTest {

    private fun knowledge(): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = "full lifecycle flow test",
            confidence = 0.95,
            source = RuntimeKnowledgeSource.EXPERIENCE,
            createdAt = System.currentTimeMillis()
        )
    }

    @Test
    fun knowledge_moves_through_full_lifecycle_flow() {
        val composition = DefaultRuntimeComposition()

        val lifecycle =
            composition.knowledgeLifecycleComposition()

        val memory =
            lifecycle.lifecycleMemory()

        val query =
            lifecycle.lifecycleHistoryQuery()

        val knowledge = knowledge()

        memory.create(knowledge)

        assertEquals(
            RuntimeKnowledgeLifecycleState.ACTIVE,
            memory.memory().getLifecycleState(knowledge)
        )

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

        assertEquals(
            3,
            query.transitionCount(knowledge)
        )
    }
}
