package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.maintenance

interface RuntimeKnowledgeMaintenanceTrigger {

    fun afterKnowledgeProduction(
        produced: Boolean
    ): RuntimeKnowledgeMaintenanceReport?
}
