package pro.liliya.core.runtime.intelligence.knowledge.selection

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.relevance.DefaultRuntimeKnowledgeRelevanceEvaluator
import pro.liliya.core.runtime.intelligence.knowledge.relevance.RuntimeKnowledgeRelevanceEvaluator

class DefaultRuntimeKnowledgeSelector(
    private val relevanceEvaluator:
        RuntimeKnowledgeRelevanceEvaluator =
        DefaultRuntimeKnowledgeRelevanceEvaluator()
) : RuntimeKnowledgeSelector {

    override fun select(
        knowledge: List<RuntimeKnowledge>,
        interpretation: String
    ): RuntimeKnowledge? {

        return selectResult(
            knowledge,
            interpretation
        ).knowledge
    }

    override fun selectResult(
        knowledge: List<RuntimeKnowledge>,
        interpretation: String
    ): RuntimeKnowledgeSelectionResult {

        val evaluated =
            knowledge.map { item ->
                item to
                    relevanceEvaluator.evaluate(
                        statement = item.statement,
                        interpretation = interpretation
                    )
            }

        val relevantKnowledge =
            evaluated
                .filter {
                    it.second.relevant
                }
                .map {
                    it.first
                }

        val selectionPool =
            if (relevantKnowledge.isEmpty()) {
                knowledge
            } else {
                relevantKnowledge
            }

        val selected =
            selectionPool.maxWithOrNull(
                compareBy<RuntimeKnowledge> {
                    it.confidence
                }.thenBy {
                    it.createdAt
                }
            )

        val selectionReason =
            when {
                selected == null ->
                    RuntimeKnowledgeSelectionReason.EMPTY

                relevantKnowledge.isNotEmpty() ->
                    RuntimeKnowledgeSelectionReason.RELEVANT_POOL

                else ->
                    RuntimeKnowledgeSelectionReason.FALLBACK_POOL
            }

        val relevanceScore =
            if (
                selectionReason ==
                    RuntimeKnowledgeSelectionReason.RELEVANT_POOL &&
                selected != null
            ) {
                relevanceEvaluator
                    .evaluate(
                        statement = selected.statement,
                        interpretation = interpretation
                    )
                    .score
            } else {
                0.0
            }

        return RuntimeKnowledgeSelectionResult(
            knowledge = selected,
            relevantPoolUsed =
                selectionReason ==
                    RuntimeKnowledgeSelectionReason.RELEVANT_POOL,
            reason =
                when (selectionReason) {
                    RuntimeKnowledgeSelectionReason.RELEVANT_POOL ->
                        "Selected from relevant knowledge pool"

                    RuntimeKnowledgeSelectionReason.FALLBACK_POOL ->
                        "Selected from fallback knowledge pool"

                    RuntimeKnowledgeSelectionReason.EMPTY ->
                        "No knowledge available for selection"
                },
            selectionReason = selectionReason,
            relevanceScore = relevanceScore
        )
    }
}
