package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.summary

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

interface RuntimeKnowledgeLifecycleSummaryQuery {

    fun summary(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeLifecycleSummary
}
