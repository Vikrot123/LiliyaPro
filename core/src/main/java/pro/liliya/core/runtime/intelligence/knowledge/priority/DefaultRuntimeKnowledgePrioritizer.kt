package pro.liliya.core.runtime.intelligence.knowledge.priority

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

class DefaultRuntimeKnowledgePrioritizer :
    RuntimeKnowledgePrioritizer {

    override fun prioritize(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgePriority {

        val level = when {
            knowledge.confidence >= 0.9 ->
                RuntimeKnowledgePriorityLevel.CRITICAL

            knowledge.confidence >= 0.7 ->
                RuntimeKnowledgePriorityLevel.HIGH

            knowledge.confidence >= 0.4 ->
                RuntimeKnowledgePriorityLevel.MEDIUM

            else ->
                RuntimeKnowledgePriorityLevel.LOW
        }

        return RuntimeKnowledgePriority(
            knowledge = knowledge,
            level = level,
            score = knowledge.confidence,
            reason = "Priority calculated from knowledge confidence"
        )
    }
}
