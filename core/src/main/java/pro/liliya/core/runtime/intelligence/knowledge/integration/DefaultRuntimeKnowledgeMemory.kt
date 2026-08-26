package pro.liliya.core.runtime.intelligence.knowledge.integration

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.association.RuntimeKnowledgeAssociationType
import pro.liliya.core.runtime.intelligence.knowledge.association.DefaultRuntimeKnowledgeAssociator
import pro.liliya.core.runtime.intelligence.knowledge.association.store.DefaultRuntimeKnowledgeAssociationStore
import pro.liliya.core.runtime.intelligence.knowledge.association.store.RuntimeKnowledgeAssociationStore
import pro.liliya.core.runtime.intelligence.knowledge.graph.DefaultRuntimeKnowledgeGraphBuilder
import pro.liliya.core.runtime.intelligence.knowledge.graph.RuntimeKnowledgeGraphNode
import pro.liliya.core.runtime.intelligence.knowledge.graph.query.DefaultRuntimeKnowledgeGraphQueryEngine
import pro.liliya.core.runtime.intelligence.knowledge.graph.query.RuntimeKnowledgeGraphQuery
import pro.liliya.core.runtime.intelligence.knowledge.graph.ranking.DefaultRuntimeKnowledgeGraphRanker
import pro.liliya.core.runtime.intelligence.knowledge.graph.ranking.RuntimeKnowledgeGraphRankingResult
import pro.liliya.core.runtime.intelligence.knowledge.graph.store.DefaultRuntimeKnowledgeGraphStore
import pro.liliya.core.runtime.intelligence.knowledge.graph.store.RuntimeKnowledgeGraphStore
import pro.liliya.core.runtime.intelligence.knowledge.store.DefaultRuntimeKnowledgeStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.integration.query.DefaultRuntimeKnowledgeLifecycleMemoryQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.integration.query.RuntimeKnowledgeLifecycleMemoryQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.query.DefaultRuntimeKnowledgeLifecycleStateQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state.RuntimeKnowledgeLifecycleStateStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state.DefaultRuntimeKnowledgeLifecycleStateStore
import pro.liliya.core.runtime.intelligence.knowledge.retrieval.lifecycle.DefaultRuntimeKnowledgeLifecycleFilter
import pro.liliya.core.runtime.intelligence.knowledge.retrieval.lifecycle.RuntimeKnowledgeLifecycleFilter

class DefaultRuntimeKnowledgeMemory(
    private val lifecycleStateStore: RuntimeKnowledgeLifecycleStateStore =
        DefaultRuntimeKnowledgeLifecycleStateStore(),

    private val lifecycleMemoryQuery: RuntimeKnowledgeLifecycleMemoryQuery =
        DefaultRuntimeKnowledgeLifecycleMemoryQuery(
            DefaultRuntimeKnowledgeLifecycleStateQuery(
                lifecycleStateStore
            )
        ),

    private val lifecycleFilter: RuntimeKnowledgeLifecycleFilter =
        DefaultRuntimeKnowledgeLifecycleFilter(
            DefaultRuntimeKnowledgeLifecycleStateQuery(
                lifecycleStateStore
            )
        ),
    private val associationStore: RuntimeKnowledgeAssociationStore =
        DefaultRuntimeKnowledgeAssociationStore(),
    private val graphStore: RuntimeKnowledgeGraphStore =
        DefaultRuntimeKnowledgeGraphStore()
) : RuntimeKnowledgeMemory {

    private val knowledgeStore =
        DefaultRuntimeKnowledgeStore()

    private val associator =
        DefaultRuntimeKnowledgeAssociator()

    private val graphBuilder =
        DefaultRuntimeKnowledgeGraphBuilder()

    private val queryEngine =
        DefaultRuntimeKnowledgeGraphQueryEngine()

    private val ranker =
        DefaultRuntimeKnowledgeGraphRanker()


    override fun remember(
        knowledge: RuntimeKnowledge
    ) {
        knowledgeStore.append(
            knowledge
        )
    }

    override fun forget(
        knowledge: RuntimeKnowledge
    ) {
        knowledgeStore.removeLast(
            knowledge
        )
    }

    override fun associate(
        source: RuntimeKnowledge,
        target: RuntimeKnowledge,
        type: RuntimeKnowledgeAssociationType
    ) {
        val association =
            associator.associate(
                source,
                target,
                type
            )

        val edge =
            graphBuilder.connect(
                source,
                target,
                type
            )

        associationStore.append(
            association
        )

        try {
            graphStore.append(
                edge
            )
        } catch (error: Throwable) {
            try {
                associationStore.removeLast(
                    association
                )
            } catch (rollbackError: Throwable) {
                error.addSuppressed(
                    rollbackError
                )
            }

            throw error
        }
    }


    override fun query(
        text: String
    ): List<RuntimeKnowledgeGraphRankingResult> {

        val nodes =
            lifecycleFilter
                .filter(
                    knowledgeStore.knowledge()
                )
                .map {
                    RuntimeKnowledgeGraphNode(
                        knowledge = it,
                        createdAt = it.createdAt
                    )
                }

        val results =
            queryEngine.query(
                RuntimeKnowledgeGraphQuery(text),
                nodes
            )

        return ranker.rank(
            results
        )
    }

    override fun getLifecycleState(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeLifecycleState? {
        return lifecycleMemoryQuery.getState(
            knowledge
        )
    }
}
