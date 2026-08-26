package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.DefaultRuntimeKnowledgeLifecycleHistoryStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.query.RuntimeKnowledgeLifecycleStateQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state.RuntimeKnowledgeLifecycleStateStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.transition.DefaultRuntimeKnowledgeLifecycleTransitionManager

class DefaultRuntimeKnowledgeLifecycleTransitionStateFailureAtomicityContractTest {

    @Test
    fun failed_state_write_must_not_leave_successful_history_entry() {

        val knowledge =
            RuntimeKnowledge(
                statement = "state failure atomicity",
                confidence = 0.8,
                source = RuntimeKnowledgeSource.CONSOLIDATION,
                createdAt = 1L
            )

        val stateStore =
            object : RuntimeKnowledgeLifecycleStateStore {

                override fun setState(
                    knowledge: RuntimeKnowledge,
                    state: RuntimeKnowledgeLifecycleState
                ) {
                    throw IllegalStateException(
                        "state write failed"
                    )
                }

                override fun getState(
                    knowledge: RuntimeKnowledge
                ): RuntimeKnowledgeLifecycleState? {
                    return null
                }
            }

        val stateQuery =
            object : RuntimeKnowledgeLifecycleStateQuery {

                override fun getState(
                    knowledge: RuntimeKnowledge
                ): RuntimeKnowledgeLifecycleState? {
                    return stateStore.getState(knowledge)
                }
            }

        val historyStore =
            DefaultRuntimeKnowledgeLifecycleHistoryStore()

        val manager =
            DefaultRuntimeKnowledgeLifecycleTransitionManager(
                stateQuery,
                stateStore,
                historyStore
            )

        assertFailsWith<IllegalStateException> {
            manager.transition(
                knowledge,
                RuntimeKnowledgeLifecycleState.ACTIVE
            )
        }

        assertEquals(
            0,
            historyStore.history(knowledge).size,
            "failed state write must not leave a successful transition history entry"
        )
    }
}
