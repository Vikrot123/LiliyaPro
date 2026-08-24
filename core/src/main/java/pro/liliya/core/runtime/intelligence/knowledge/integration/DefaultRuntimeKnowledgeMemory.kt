package pro.liliya.core.runtime.intelligence.knowledge.integration

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.association.RuntimeKnowledgeAssociationType
import pro.liliya.core.runtime.intelligence.knowledge.association.DefaultRuntimeKnowledgeAssociator
import pro.liliya.core.runtime.intelligence.knowledge.association.store.DefaultRuntimeKnowledgeAssociationStore
import pro.liliya.core.runtime.intelligence.knowledge.graph.DefaultRuntimeKnowledgeGraphBuilder
import pro.liliya.core.runtime.intelligence.knowledge.graph.RuntimeKnowledgeGraphNode
import pro.liliya.core.runtime.intelligence.knowledge.graph.query.DefaultRuntimeKnowledgeGraphQueryEngine
import pro.liliya.core.runtime.intelligence.knowledge.graph.query.RuntimeKnowledgeGraphQuery
import pro.liliya.core.runtime.intelligence.knowledge.graph.ranking.DefaultRuntimeKnowledgeGraphRanker
import pro.liliya.core.runtime.intelligence.knowledge.graph.ranking.RuntimeKnowledgeGraphRankingResult
import pro.liliya.core.runtime.intelligence.knowledge.graph.store.DefaultRuntimeKnowledgeGraphStore
import pro.liliya.core.runtime.intelligence.knowledge.store.DefaultRuntimeKnowledgeStore

class DefaultRuntimeKnowledgeMemory :
    RuntimeKnowledgeMemory {

    private val knowledgeStore =
        DefaultRuntimeKnowledgeStore()

    private val associationStore =
        DefaultRuntimeKnowledgeAssociationStore()

    private val graphStore =
        DefaultRuntimeKnowledgeGraphStore()

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

    override fun associate(
        source: RuntimeKnowledge,
        target: RuntimeKnowledge,
        type: RuntimeKnowledgeAssociationType
    ) {
        associationStore.append(
            associator.associate(
                source,
                target,
                type
            )
        )

        graphStore.append(
            graphBuilder.connect(
                source,
                target,
                type
            )
        )
    }

    override fun query(
        text: String
    ): List<RuntimeKnowledgeGraphRankingResult> {

        val nodes =
            knowledgeStore
                .knowledge()
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
}
