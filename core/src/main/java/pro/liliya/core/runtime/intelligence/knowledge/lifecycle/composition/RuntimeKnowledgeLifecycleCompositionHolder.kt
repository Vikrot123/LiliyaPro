package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.composition

interface RuntimeKnowledgeLifecycleCompositionHolder {

    fun composition():
        RuntimeKnowledgeLifecycleComposition

    fun reset()
}
