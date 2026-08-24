package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.decision

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

interface RuntimeKnowledgeLifecycleDecisionQuery {

    fun decide(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeLifecycleDecision
}
