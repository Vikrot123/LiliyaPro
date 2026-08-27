package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.maintenance

import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.decision.RuntimeKnowledgeLifecycleDecisionQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.executor.RuntimeKnowledgeLifecycleExecutor
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.integration.RuntimeKnowledgeLifecycleMemory

class DefaultRuntimeKnowledgeMaintenanceService(
    private val lifecycleMemory: RuntimeKnowledgeLifecycleMemory,
    private val decisionQuery: RuntimeKnowledgeLifecycleDecisionQuery,
    private val executor: RuntimeKnowledgeLifecycleExecutor
) : RuntimeKnowledgeMaintenanceService {

    override fun maintain():
        RuntimeKnowledgeMaintenanceReport {

        val knowledge =
            lifecycleMemory
                .memory()
                .availableKnowledge()
                .toList()

        val outcomes =
            knowledge.map { item ->
                val decision =
                    decisionQuery.decide(item)

                val execution =
                    executor.execute(item)

                RuntimeKnowledgeMaintenanceOutcome(
                    knowledge = item,
                    decision = decision,
                    executed = execution.executed
                )
            }

        return RuntimeKnowledgeMaintenanceReport(
            outcomes = outcomes
        )
    }
}
