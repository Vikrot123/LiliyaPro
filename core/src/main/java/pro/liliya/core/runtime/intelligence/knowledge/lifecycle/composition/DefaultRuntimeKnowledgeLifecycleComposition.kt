package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.composition

import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.decision.DefaultRuntimeKnowledgeLifecycleDecisionQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.executor.DefaultRuntimeKnowledgeLifecycleExecutor
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.DefaultRuntimeKnowledgeLifecycleHistoryStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.query.DefaultRuntimeKnowledgeLifecycleHistoryQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.query.RuntimeKnowledgeLifecycleHistoryQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.pipeline.DefaultRuntimeKnowledgeLifecyclePipeline
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.DefaultRuntimeKnowledgeLifecycleService
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.RuntimeKnowledgeLifecycleService
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.orchestrator.DefaultRuntimeKnowledgeLifecycleOrchestrator
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.query.DefaultRuntimeKnowledgeLifecycleStateQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state.DefaultRuntimeKnowledgeLifecycleStateStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.summary.DefaultRuntimeKnowledgeLifecycleSummaryQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.transition.DefaultRuntimeKnowledgeLifecycleTransitionManager
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.integration.DefaultRuntimeKnowledgeLifecycleMemory
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.integration.RuntimeKnowledgeLifecycleMemory

class DefaultRuntimeKnowledgeLifecycleComposition :
    RuntimeKnowledgeLifecycleComposition {

    private val stateStore =
        DefaultRuntimeKnowledgeLifecycleStateStore()

    private val historyStore =
        DefaultRuntimeKnowledgeLifecycleHistoryStore()

    private val stateQuery =
        DefaultRuntimeKnowledgeLifecycleStateQuery(
            stateStore
        )

    private val historyQuery =
        DefaultRuntimeKnowledgeLifecycleHistoryQuery(
            historyStore
        )

    private val summaryQuery =
        DefaultRuntimeKnowledgeLifecycleSummaryQuery(
            historyQuery
        )

    private val decisionQuery =
        DefaultRuntimeKnowledgeLifecycleDecisionQuery(
            summaryQuery
        )

    private val transitionManager =
        DefaultRuntimeKnowledgeLifecycleTransitionManager(
            stateQuery,
            stateStore,
            historyStore
        )

    private val executor =
        DefaultRuntimeKnowledgeLifecycleExecutor(
            decisionQuery,
            transitionManager
        )

    private val orchestrator =
        DefaultRuntimeKnowledgeLifecycleOrchestrator(
            executor
        )

    private val pipeline =
        DefaultRuntimeKnowledgeLifecyclePipeline(
            orchestrator
        )

    private val lifecycleMemory:
        RuntimeKnowledgeLifecycleMemory =
        DefaultRuntimeKnowledgeLifecycleMemory(
            stateStore,
            transitionManager
        )

    private var service:
        RuntimeKnowledgeLifecycleService =
        DefaultRuntimeKnowledgeLifecycleService(
            pipeline
        )

    override fun lifecycleService():
        RuntimeKnowledgeLifecycleService {
        return service
    }

    override fun lifecycleMemory():
        RuntimeKnowledgeLifecycleMemory {
        return lifecycleMemory
    }

    override fun lifecycleHistoryQuery():
        RuntimeKnowledgeLifecycleHistoryQuery {
        return historyQuery
    }

    override fun reset() {
        service =
            DefaultRuntimeKnowledgeLifecycleService(
                pipeline
            )
    }
}
