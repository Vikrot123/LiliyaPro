package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.query.DefaultRuntimeKnowledgeLifecycleStateQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state.DefaultRuntimeKnowledgeLifecycleStateStore

class DefaultRuntimeKnowledgeLifecycleStateQueryContractTest {

    private fun knowledge(): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = "runtime lifecycle query",
            confidence = 0.8,
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = 1L
        )
    }

    @Test
    fun query_returns_current_knowledge_state() {

        val store =
            DefaultRuntimeKnowledgeLifecycleStateStore()

        val knowledge =
            knowledge()

        store.setState(
            knowledge,
            RuntimeKnowledgeLifecycleState.ACTIVE
        )

        val query =
            DefaultRuntimeKnowledgeLifecycleStateQuery(
                store
            )

        assertEquals(
            RuntimeKnowledgeLifecycleState.ACTIVE,
            query.getState(
                knowledge
            )
        )
    }
}
