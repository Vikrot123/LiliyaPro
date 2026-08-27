package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.supersession.query

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

data class RuntimeKnowledgeSupersessionTrace(
    val startingKnowledge: RuntimeKnowledge,
    val chain: List<RuntimeKnowledge>,
    val currentKnowledge: RuntimeKnowledge,
    val cycleDetected: Boolean
)
