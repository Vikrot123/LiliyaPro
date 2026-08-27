package pro.liliya.core.runtime.intelligence.knowledge.integration

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.conflict.DefaultRuntimeKnowledgeConflictResolver
import pro.liliya.core.runtime.intelligence.knowledge.conflict.RuntimeKnowledgeConflict
import pro.liliya.core.runtime.intelligence.knowledge.conflict.RuntimeKnowledgeConflictType
import pro.liliya.core.runtime.intelligence.knowledge.hygiene.DefaultRuntimeKnowledgeHygieneEvaluator
import pro.liliya.core.runtime.intelligence.knowledge.hygiene.RuntimeKnowledgeHygieneAction
import pro.liliya.core.runtime.intelligence.knowledge.hygiene.RuntimeKnowledgeHygieneResult
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
import pro.liliya.core.runtime.intelligence.knowledge.retrieval.DefaultRuntimeKnowledgeRetriever
import pro.liliya.core.runtime.intelligence.knowledge.retrieval.RuntimeKnowledgeQuery
import pro.liliya.core.runtime.intelligence.knowledge.retrieval.RuntimeKnowledgeRetrievalResult
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

    private val hygieneEvaluator =
        DefaultRuntimeKnowledgeHygieneEvaluator()

    private val conflictResolver =
        DefaultRuntimeKnowledgeConflictResolver()

    private val semanticRetriever =
        DefaultRuntimeKnowledgeRetriever()

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
        rememberWithHygiene(
            knowledge
        )
    }

    override fun rememberWithHygiene(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeHygieneResult {

        return synchronized(knowledgeStore) {
            val existing =
                hygieneEvaluator.findEquivalent(
                    knowledge = knowledge,
                    existing = knowledgeStore.knowledge()
                )

            if (existing == null) {
                knowledgeStore.append(
                    knowledge
                )

                RuntimeKnowledgeHygieneResult(
                    action =
                        RuntimeKnowledgeHygieneAction.ADDED,
                    requestedKnowledge = knowledge,
                    retainedKnowledge = knowledge
                )
            } else if (
                existing.confidence == knowledge.confidence
            ) {
                RuntimeKnowledgeHygieneResult(
                    action =
                        RuntimeKnowledgeHygieneAction.DUPLICATE_SUPPRESSED,
                    requestedKnowledge = knowledge,
                    retainedKnowledge = existing
                )
            } else {
                val resolution =
                    conflictResolver.resolve(
                        RuntimeKnowledgeConflict(
                            first = existing,
                            second = knowledge,
                            type =
                                RuntimeKnowledgeConflictType.CONFIDENCE_DIFFERENCE,
                            createdAt =
                                System.currentTimeMillis()
                        )
                    )

                RuntimeKnowledgeHygieneResult(
                    action =
                        RuntimeKnowledgeHygieneAction.CONFLICT_SUPPRESSED,
                    requestedKnowledge = knowledge,
                    retainedKnowledge = existing,
                    conflictResolution = resolution
                )
            }
        }
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

    override fun availableKnowledge(): List<RuntimeKnowledge> {
        return lifecycleFilter.filter(
            knowledgeStore.knowledge()
        )
    }

    override fun retrieveRelevant(
        text: String
    ): List<RuntimeKnowledgeRetrievalResult> {
        return semanticRetriever.retrieve(
            query = RuntimeKnowledgeQuery(text),
            knowledge = availableKnowledge()
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
