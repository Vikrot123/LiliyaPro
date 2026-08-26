package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.integration.DefaultRuntimeKnowledgeMemory
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.DefaultRuntimeKnowledgeLifecycleHistoryStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.integration.DefaultRuntimeKnowledgeLifecycleMemory
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state.DefaultRuntimeKnowledgeLifecycleStateStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.transition.RuntimeKnowledgeLifecycleTransitionManager

class DefaultRuntimeKnowledgeLifecycleMemoryRollbackOwnershipContractTest {

    @Test
    fun failed_create_rollback_preserves_preexisting_equal_knowledge() {

        val stateStore =
            DefaultRuntimeKnowledgeLifecycleStateStore()

        val knowledgeMemory =
            DefaultRuntimeKnowledgeMemory(
                lifecycleStateStore = stateStore
            )

        val knowledge =
            RuntimeKnowledge(
                statement = "preexisting equal knowledge",
                confidence = 0.9,
                source = RuntimeKnowledgeSource.EXPERIENCE,
                createdAt = 1L
            )

        // Existing record must survive rollback of the second append.
        knowledgeMemory.remember(
            knowledge
        )

        val transitionManager =
            object : RuntimeKnowledgeLifecycleTransitionManager {

                override fun transition(
                    knowledge: RuntimeKnowledge,
                    target: RuntimeKnowledgeLifecycleState
                ): Boolean {
                    throw IllegalStateException(
                        "initial transition failed"
                    )
                }
            }

        val lifecycleMemory =
            DefaultRuntimeKnowledgeLifecycleMemory(
                stateStore = stateStore,
                historyStore =
                    DefaultRuntimeKnowledgeLifecycleHistoryStore(),
                transitionManager = transitionManager,
                knowledgeMemory = knowledgeMemory
            )

        assertFailsWith<IllegalStateException> {
            lifecycleMemory.create(
                knowledge
            )
        }

        val results =
            knowledgeMemory.query(
                "preexisting"
            )

        assertEquals(
            1,
            results.size,
            "rollback must remove only the failed create append and preserve preexisting equal knowledge"
        )
    }
}
