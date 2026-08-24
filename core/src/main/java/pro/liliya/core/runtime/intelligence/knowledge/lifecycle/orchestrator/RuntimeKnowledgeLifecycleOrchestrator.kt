package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.orchestrator

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

interface RuntimeKnowledgeLifecycleOrchestrator {

    fun process(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeLifecycleOrchestrationResult
}
