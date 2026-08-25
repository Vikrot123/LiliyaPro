package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state.DefaultRuntimeKnowledgeLifecycleStateStore
import pro.liliya.core.runtime.intelligence.knowledge.integration.DefaultRuntimeKnowledgeMemory
import kotlin.test.assertNotSame
import kotlin.test.assertSame

import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.composition.DefaultRuntimeKnowledgeLifecycleComposition

class DefaultRuntimeKnowledgeLifecycleCompositionContractTest {

    @Test
    fun composition_returns_same_service_instance() {
        val composition =
            DefaultRuntimeKnowledgeLifecycleComposition(DefaultRuntimeKnowledgeMemory(), DefaultRuntimeKnowledgeLifecycleStateStore())

        val first =
            composition.lifecycleService()

        val second =
            composition.lifecycleService()

        assertSame(
            first,
            second
        )
    }

    @Test
    fun composition_reset_creates_new_service_instance() {
        val composition =
            DefaultRuntimeKnowledgeLifecycleComposition(DefaultRuntimeKnowledgeMemory(), DefaultRuntimeKnowledgeLifecycleStateStore())

        val first =
            composition.lifecycleService()

        composition.reset()

        val second =
            composition.lifecycleService()

        assertNotSame(
            first,
            second
        )
    }
    @Test
    fun composition_reset_preserves_memory_and_history_query_identity() {
        val composition = DefaultRuntimeKnowledgeLifecycleComposition(
            DefaultRuntimeKnowledgeMemory(),
            DefaultRuntimeKnowledgeLifecycleStateStore()
        )

        val beforeMemory = composition.lifecycleMemory()
        val beforeHistoryQuery = composition.lifecycleHistoryQuery()

        composition.reset()

        val afterMemory = composition.lifecycleMemory()
        val afterHistoryQuery = composition.lifecycleHistoryQuery()

        assertSame(beforeMemory, afterMemory)
        assertSame(beforeHistoryQuery, afterHistoryQuery)
    }

    @Test
    fun composition_reset_preserves_history_continuity() {
        val composition = DefaultRuntimeKnowledgeLifecycleComposition(
            DefaultRuntimeKnowledgeMemory(),
            DefaultRuntimeKnowledgeLifecycleStateStore()
        )

        val knowledge = RuntimeKnowledge(
            statement = "composition history continuity",
            confidence = 0.9,
            source = RuntimeKnowledgeSource.EXPERIENCE,
            createdAt = 1L
        )

        val memory = composition.lifecycleMemory()
        val historyQuery = composition.lifecycleHistoryQuery()

        memory.create(knowledge)
        memory.revise(knowledge)

        assertSame(historyQuery, composition.lifecycleHistoryQuery())

        val beforeResetCount = historyQuery.transitionCount(knowledge)
        assertEquals(2, beforeResetCount)

        composition.reset()

        val afterResetQuery = composition.lifecycleHistoryQuery()

        assertSame(historyQuery, afterResetQuery)
        assertEquals(2, afterResetQuery.transitionCount(knowledge))
    }


}
