package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertSame

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource

class RuntimeCompositionKnowledgeLifecyclePrepareIsolationContractTest {

    @Test
    fun prepare_runtime_keeps_knowledge_lifecycle_composition_owner() {
        val composition =
            DefaultRuntimeComposition()

        val before =
            composition.knowledgeLifecycleComposition()

        composition.prepareRuntime()

        val after =
            composition.knowledgeLifecycleComposition()

        assertSame(
            before,
            after
        )
    }
    @Test
    fun prepare_runtime_preserves_knowledge_lifecycle_history() {
        val composition = DefaultRuntimeComposition()
        val lifecycle = composition.knowledgeLifecycleComposition()

        val knowledge = RuntimeKnowledge(
            statement = "prepare knowledge continuity",
            confidence = 0.9,
            source = RuntimeKnowledgeSource.EXPERIENCE,
            createdAt = 1L
        )

        val beforeService = lifecycle.lifecycleService()
        val memory = lifecycle.lifecycleMemory()
        val historyQuery = lifecycle.lifecycleHistoryQuery()

        memory.create(knowledge)
        memory.revise(knowledge)

        assertEquals(
            2,
            historyQuery.transitionCount(knowledge)
        )

        composition.prepareRuntime()

        val afterService = lifecycle.lifecycleService()

        assertNotSame(beforeService, afterService)
        assertSame(memory, lifecycle.lifecycleMemory())
        assertSame(historyQuery, lifecycle.lifecycleHistoryQuery())

        assertEquals(
            2,
            lifecycle.lifecycleHistoryQuery()
                .transitionCount(knowledge)
        )
    }

}
