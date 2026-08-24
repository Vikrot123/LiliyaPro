package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.transition

import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState

data class RuntimeKnowledgeLifecycleTransition(
    val from: RuntimeKnowledgeLifecycleState?,
    val to: RuntimeKnowledgeLifecycleState
)
