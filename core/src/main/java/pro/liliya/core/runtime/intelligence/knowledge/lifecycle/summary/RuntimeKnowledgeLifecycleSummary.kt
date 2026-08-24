package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.summary

import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState

data class RuntimeKnowledgeLifecycleSummary(

    val currentState: RuntimeKnowledgeLifecycleState?,

    val transitionCount: Int,

    val firstState: RuntimeKnowledgeLifecycleState?,

    val lastState: RuntimeKnowledgeLifecycleState?

)
