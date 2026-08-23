package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.DefaultRuntimeKnowledgeLifecycleManager
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState

class DefaultRuntimeKnowledgeLifecycleManagerContractTest {

    private fun knowledge(): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = "runtime pattern",
            confidence = 0.8,
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = 1L
        )
    }

    @Test
    fun create_starts_knowledge_as_active() {

        val lifecycle =
            DefaultRuntimeKnowledgeLifecycleManager()
                .create(
                    knowledge()
                )

        assertEquals(
            RuntimeKnowledgeLifecycleState.ACTIVE,
            lifecycle.state
        )
    }

    @Test
    fun transition_changes_lifecycle_state() {

        val lifecycle =
            DefaultRuntimeKnowledgeLifecycleManager()
                .create(
                    knowledge()
                )

        val updated =
            DefaultRuntimeKnowledgeLifecycleManager()
                .transition(
                    lifecycle,
                    RuntimeKnowledgeLifecycleState.REVIEW
                )

        assertEquals(
            RuntimeKnowledgeLifecycleState.REVIEW,
            updated.state
        )
    }
}
