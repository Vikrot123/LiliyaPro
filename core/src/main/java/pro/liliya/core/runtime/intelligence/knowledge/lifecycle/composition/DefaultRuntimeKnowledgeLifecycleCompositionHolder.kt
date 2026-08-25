package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.composition

import pro.liliya.core.runtime.intelligence.knowledge.integration.RuntimeKnowledgeMemory
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state.RuntimeKnowledgeLifecycleStateStore

class DefaultRuntimeKnowledgeLifecycleCompositionHolder(
    private val knowledgeMemory: RuntimeKnowledgeMemory,
    private val knowledgeLifecycleStateStore: RuntimeKnowledgeLifecycleStateStore
) : RuntimeKnowledgeLifecycleCompositionHolder {

    private var currentComposition:
        RuntimeKnowledgeLifecycleComposition =
        DefaultRuntimeKnowledgeLifecycleComposition(
            knowledgeMemory,
            knowledgeLifecycleStateStore
        )

    override fun composition():
        RuntimeKnowledgeLifecycleComposition {
        return currentComposition
    }

    override fun reset() {
        currentComposition =
            DefaultRuntimeKnowledgeLifecycleComposition(
                knowledgeMemory,
                knowledgeLifecycleStateStore
            )
    }
}
