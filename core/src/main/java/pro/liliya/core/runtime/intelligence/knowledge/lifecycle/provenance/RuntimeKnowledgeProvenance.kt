package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.provenance

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

data class RuntimeKnowledgeProvenance(
    val startingKnowledge: RuntimeKnowledge,
    val currentKnowledge: RuntimeKnowledge,
    val chain: List<RuntimeKnowledge>,
    val steps: List<RuntimeKnowledgeProvenanceStep>,
    val cycleDetected: Boolean
)
