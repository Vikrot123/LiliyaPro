package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.supersession.query

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.supersession.RuntimeKnowledgeSupersessionHistory

class DefaultRuntimeKnowledgeSupersessionQuery(
    private val history:
        RuntimeKnowledgeSupersessionHistory
) : RuntimeKnowledgeSupersessionQuery {

    override fun trace(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeSupersessionTrace {

        val chain =
            mutableListOf(
                knowledge
            )

        val visited =
            mutableSetOf(
                knowledge
            )

        var current =
            knowledge

        var cycleDetected =
            false

        while (true) {
            val replacement =
                history
                    .replacementsOf(current)
                    .lastOrNull()
                    ?.replacementKnowledge
                    ?: break

            if (!visited.add(replacement)) {
                cycleDetected = true
                break
            }

            chain += replacement
            current = replacement
        }

        return RuntimeKnowledgeSupersessionTrace(
            startingKnowledge = knowledge,
            chain = chain.toList(),
            currentKnowledge = current,
            cycleDetected = cycleDetected
        )
    }

    override fun currentKnowledge(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledge {

        return trace(
            knowledge
        ).currentKnowledge
    }
}
