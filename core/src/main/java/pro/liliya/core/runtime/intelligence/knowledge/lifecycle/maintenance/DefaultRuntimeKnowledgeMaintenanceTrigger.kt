package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.maintenance

class DefaultRuntimeKnowledgeMaintenanceTrigger(
    private val maintenanceService:
        RuntimeKnowledgeMaintenanceService
) : RuntimeKnowledgeMaintenanceTrigger {

    override fun afterKnowledgeProduction(
        produced: Boolean
    ): RuntimeKnowledgeMaintenanceReport? {

        if (!produced) {
            return null
        }

        return maintenanceService.maintain()
    }
}
