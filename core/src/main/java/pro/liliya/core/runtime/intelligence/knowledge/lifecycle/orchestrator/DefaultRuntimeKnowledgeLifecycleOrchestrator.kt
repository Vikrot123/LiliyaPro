package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.orchestrator

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.executor.RuntimeKnowledgeLifecycleExecutor

class DefaultRuntimeKnowledgeLifecycleOrchestrator(
    private val executor: RuntimeKnowledgeLifecycleExecutor
) : RuntimeKnowledgeLifecycleOrchestrator {

    override fun process(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeLifecycleOrchestrationResult {

        return RuntimeKnowledgeLifecycleOrchestrationResult(
            executionResult = executor.execute(knowledge)
        )
    }
}
