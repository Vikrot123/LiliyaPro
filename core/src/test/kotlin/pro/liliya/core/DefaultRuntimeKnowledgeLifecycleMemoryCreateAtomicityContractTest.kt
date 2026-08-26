package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.association.RuntimeKnowledgeAssociationType
import pro.liliya.core.runtime.intelligence.knowledge.graph.ranking.RuntimeKnowledgeGraphRankingResult
import pro.liliya.core.runtime.intelligence.knowledge.integration.RuntimeKnowledgeMemory
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.DefaultRuntimeKnowledgeLifecycleHistoryStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.integration.DefaultRuntimeKnowledgeLifecycleMemory
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state.DefaultRuntimeKnowledgeLifecycleStateStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.transition.RuntimeKnowledgeLifecycleTransitionManager

class DefaultRuntimeKnowledgeLifecycleMemoryCreateAtomicityContractTest {

    @Test
    fun failed_initial_transition_must_not_leave_knowledge_remembered() {

        val remembered =
            mutableListOf<RuntimeKnowledge>()

        val memory =
            object : RuntimeKnowledgeMemory {

                override fun remember(
                    knowledge: RuntimeKnowledge
                ) {
                    remembered += knowledge
                }

                override fun forget(
                    knowledge: RuntimeKnowledge
                ) {
                    val index =
                        remembered.indexOfLast {
                            it == knowledge
                        }

                    if (index >= 0) {
                        remembered.removeAt(index)
                    }
                }

                override fun associate(
                    source: RuntimeKnowledge,
                    target: RuntimeKnowledge,
                    type: RuntimeKnowledgeAssociationType
                ) {
                }

                override fun query(
                    text: String
                ): List<RuntimeKnowledgeGraphRankingResult> {
                    return emptyList()
                }

                override fun getLifecycleState(
                    knowledge: RuntimeKnowledge
                ): RuntimeKnowledgeLifecycleState? {
                    return null
                }
            }

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
                stateStore =
                    DefaultRuntimeKnowledgeLifecycleStateStore(),
                historyStore =
                    DefaultRuntimeKnowledgeLifecycleHistoryStore(),
                transitionManager =
                    transitionManager,
                knowledgeMemory =
                    memory
            )

        val knowledge =
            RuntimeKnowledge(
                statement = "atomic lifecycle memory create",
                confidence = 0.9,
                source = RuntimeKnowledgeSource.EXPERIENCE,
                createdAt = 1L
            )

        assertFailsWith<IllegalStateException> {
            lifecycleMemory.create(
                knowledge
            )
        }

        assertFalse(
            remembered.contains(knowledge),
            "failed initial lifecycle transition must not leave knowledge remembered"
        )
    }
    @Test
    fun rejected_initial_transition_must_not_leave_knowledge_remembered() {

        val remembered =
            mutableListOf<RuntimeKnowledge>()

        val memory =
            object : RuntimeKnowledgeMemory {

                override fun remember(
                    knowledge: RuntimeKnowledge
                ) {
                    remembered += knowledge
                }

                override fun forget(
                    knowledge: RuntimeKnowledge
                ) {
                    val index =
                        remembered.indexOfLast {
                            it == knowledge
                        }

                    if (index >= 0) {
                        remembered.removeAt(index)
                    }
                }

                override fun associate(
                    source: RuntimeKnowledge,
                    target: RuntimeKnowledge,
                    type: RuntimeKnowledgeAssociationType
                ) {
                }

                override fun query(
                    text: String
                ): List<RuntimeKnowledgeGraphRankingResult> {
                    return emptyList()
                }

                override fun getLifecycleState(
                    knowledge: RuntimeKnowledge
                ): RuntimeKnowledgeLifecycleState? {
                    return null
                }
            }

        val transitionManager =
            object : RuntimeKnowledgeLifecycleTransitionManager {

                override fun transition(
                    knowledge: RuntimeKnowledge,
                    target: RuntimeKnowledgeLifecycleState
                ): Boolean {
                    return false
                }
            }

        val lifecycleMemory =
            DefaultRuntimeKnowledgeLifecycleMemory(
                stateStore =
                    DefaultRuntimeKnowledgeLifecycleStateStore(),
                historyStore =
                    DefaultRuntimeKnowledgeLifecycleHistoryStore(),
                transitionManager =
                    transitionManager,
                knowledgeMemory =
                    memory
            )

        val knowledge =
            RuntimeKnowledge(
                statement = "rejected lifecycle memory create",
                confidence = 0.9,
                source = RuntimeKnowledgeSource.EXPERIENCE,
                createdAt = 2L
            )

        lifecycleMemory.create(
            knowledge
        )

        assertFalse(
            remembered.contains(knowledge),
            "rejected initial lifecycle transition must not leave knowledge remembered"
        )
    }

}
