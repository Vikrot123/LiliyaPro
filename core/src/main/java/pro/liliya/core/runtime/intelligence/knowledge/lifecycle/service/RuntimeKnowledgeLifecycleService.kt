package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge


interface RuntimeKnowledgeLifecycleService {

    fun processKnowledge(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeLifecycleServiceResult
}
