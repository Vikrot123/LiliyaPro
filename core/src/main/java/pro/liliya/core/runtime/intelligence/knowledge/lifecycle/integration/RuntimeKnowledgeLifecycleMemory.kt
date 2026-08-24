package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.integration

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.integration.RuntimeKnowledgeMemory

interface RuntimeKnowledgeLifecycleMemory {

    fun create(
        knowledge: RuntimeKnowledge
    )

    fun activate(
        knowledge: RuntimeKnowledge
    )

    fun revise(
        knowledge: RuntimeKnowledge
    )

    fun archive(
        knowledge: RuntimeKnowledge
    )

    fun memory(): RuntimeKnowledgeMemory
}
