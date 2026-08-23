package pro.liliya.core.runtime.intelligence.knowledge.priority

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

interface RuntimeKnowledgePrioritizer {

    fun prioritize(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgePriority
}
