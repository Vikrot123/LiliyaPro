package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertFailsWith
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.RuntimeKnowledgeLifecycleHistoryEntry
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.RuntimeKnowledgeLifecycleHistoryStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.query.DefaultRuntimeKnowledgeLifecycleStateQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state.DefaultRuntimeKnowledgeLifecycleStateStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.transition.DefaultRuntimeKnowledgeLifecycleTransitionManager

class DefaultRuntimeKnowledgeLifecycleTransitionAtomicityContractTest {

    @Test
    fun failed_history_append_must_not_leave_transitioned_state() {

        val stateStore =
            DefaultRuntimeKnowledgeLifecycleStateStore()

        val historyStore =
            object : RuntimeKnowledgeLifecycleHistoryStore {

                override fun append(
                    knowledge: RuntimeKnowledge,
                    entry: RuntimeKnowledgeLifecycleHistoryEntry
                ) {
                    throw IllegalStateException(
                        "history append failed"
                    )
                }

                override fun history(
                    knowledge: RuntimeKnowledge
                ): List<RuntimeKnowledgeLifecycleHistoryEntry> {
                    return emptyList()
                }

                override fun removeLast(
                    knowledge: RuntimeKnowledge,
                    entry: RuntimeKnowledgeLifecycleHistoryEntry
                ) {
                }
            }

        val manager =
            DefaultRuntimeKnowledgeLifecycleTransitionManager(
                DefaultRuntimeKnowledgeLifecycleStateQuery(
                    stateStore
                ),
                stateStore,
                historyStore
            )

        val knowledge =
            RuntimeKnowledge(
                statement = "atomic transition knowledge",
                confidence = 0.8,
                source = RuntimeKnowledgeSource.CONSOLIDATION,
                createdAt = 1L
            )

        assertFailsWith<IllegalStateException> {
            manager.transition(
                knowledge,
                RuntimeKnowledgeLifecycleState.ACTIVE
            )
        }

        assertNull(
            stateStore.getState(knowledge),
            "failed history append must not leave transitioned state"
        )
    }
}
