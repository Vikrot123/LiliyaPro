package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.provenance

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.RuntimeKnowledgeLifecycleHistoryEntry

data class RuntimeKnowledgeProvenanceStep(
    val knowledge: RuntimeKnowledge,
    val source: RuntimeKnowledgeSource,
    val lifecycleHistory:
        List<RuntimeKnowledgeLifecycleHistoryEntry>
)
