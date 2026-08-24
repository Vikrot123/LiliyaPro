package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.executor

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

interface RuntimeKnowledgeLifecycleExecutor {

    fun execute(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeLifecycleExecutionResult
}
