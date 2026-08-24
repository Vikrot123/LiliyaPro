package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.composition

class DefaultRuntimeKnowledgeLifecycleCompositionHolder :
    RuntimeKnowledgeLifecycleCompositionHolder {

    private var currentComposition:
        RuntimeKnowledgeLifecycleComposition =
        DefaultRuntimeKnowledgeLifecycleComposition()

    override fun composition():
        RuntimeKnowledgeLifecycleComposition {
        return currentComposition
    }

    override fun reset() {
        currentComposition =
            DefaultRuntimeKnowledgeLifecycleComposition()
    }
}
