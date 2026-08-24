package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.integration.query.DefaultRuntimeKnowledgeLifecycleMemoryQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.query.DefaultRuntimeKnowledgeLifecycleStateQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state.DefaultRuntimeKnowledgeLifecycleStateStore

class DefaultRuntimeKnowledgeLifecycleMemoryQueryContractTest {

    private fun knowledge(): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = "runtime lifecycle memory query",
            confidence = 0.8,
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = 1L
        )
    }

    @Test
    fun memory_query_returns_current_lifecycle_state() {

        val store =
            DefaultRuntimeKnowledgeLifecycleStateStore()

        val knowledge =
            knowledge()

        store.setState(
            knowledge,
            RuntimeKnowledgeLifecycleState.ACTIVE
        )

        val stateQuery =
            DefaultRuntimeKnowledgeLifecycleStateQuery(
                store
            )

        val memoryQuery =
            DefaultRuntimeKnowledgeLifecycleMemoryQuery(
                stateQuery
            )

        assertEquals(
            RuntimeKnowledgeLifecycleState.ACTIVE,
            memoryQuery.getState(
                knowledge
            )
        )
    }
}
