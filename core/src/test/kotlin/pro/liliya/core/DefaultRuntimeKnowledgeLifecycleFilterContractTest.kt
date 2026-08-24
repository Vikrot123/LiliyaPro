package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.query.RuntimeKnowledgeLifecycleStateQuery
import pro.liliya.core.runtime.intelligence.knowledge.retrieval.lifecycle.DefaultRuntimeKnowledgeLifecycleFilter

class DefaultRuntimeKnowledgeLifecycleFilterContractTest {

    private fun knowledge(
        statement: String
    ): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = statement,
            confidence = 0.8,
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = 1L
        )
    }

    @Test
    fun archived_knowledge_is_removed_from_retrieval() {

        val active = knowledge("active knowledge")
        val archived = knowledge("archived knowledge")

        val query =
            object : RuntimeKnowledgeLifecycleStateQuery {

                override fun getState(
                    knowledge: RuntimeKnowledge
                ): RuntimeKnowledgeLifecycleState? {

                    return when (knowledge.statement) {
                        "archived knowledge" ->
                            RuntimeKnowledgeLifecycleState.ARCHIVED

                        else ->
                            RuntimeKnowledgeLifecycleState.ACTIVE
                    }
                }
            }

        val result =
            DefaultRuntimeKnowledgeLifecycleFilter(query)
                .filter(
                    listOf(
                        active,
                        archived
                    )
                )

        assertEquals(
            listOf(active),
            result
        )
    }
}
