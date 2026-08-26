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
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.observer.registry.DefaultRuntimeKnowledgeLifecycleObserverRegistryHolder
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.observer.RuntimeKnowledgeLifecycleObserver
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.observer.registry.RuntimeKnowledgeLifecycleObserverRegistryHolder
import pro.liliya.core.runtime.intelligence.knowledge.integration.RuntimeKnowledgeMemory
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state.RuntimeKnowledgeLifecycleStateStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.provider.DefaultRuntimeKnowledgeLifecycleObserverProvider

private fun knowledgeLifecycleCompositionLogger() =
    pro.liliya.core.logging.LoggerFactory.create(
        module = "CORE",
        component = "DefaultRuntimeKnowledgeLifecycleComposition",
        method = "registerObserver"
    )

class DefaultRuntimeKnowledgeLifecycleComposition(
    private val knowledgeMemory: RuntimeKnowledgeMemory,
    private val knowledgeLifecycleStateStore:
        RuntimeKnowledgeLifecycleStateStore
) : RuntimeKnowledgeLifecycleComposition {

    private val stateStore =
        knowledgeLifecycleStateStore

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
            stateStore = stateStore,
            historyStore = historyStore,
            transitionManager = transitionManager,
            knowledgeMemory = knowledgeMemory
        )

    private val observerRegistryHolder:
        RuntimeKnowledgeLifecycleObserverRegistryHolder =
        DefaultRuntimeKnowledgeLifecycleObserverRegistryHolder()

    private val observerProvider =
        DefaultRuntimeKnowledgeLifecycleObserverProvider(
            observerRegistryHolder
        )

    private var service:
        RuntimeKnowledgeLifecycleService =
        DefaultRuntimeKnowledgeLifecycleService(
            pipeline,
            observerProvider = observerProvider
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

    override fun registerLifecycleObserver(
        observer: RuntimeKnowledgeLifecycleObserver
    ) {
        knowledgeLifecycleCompositionLogger().debug(
            pro.liliya.core.logging.LoggerMarkers.METHOD_ENTER,
            "REGISTER_OBSERVER_CALL"
        )

        observerRegistryHolder
            .registry()
            .register(observer)
    }

    override fun unregisterLifecycleObserver(
        observer: RuntimeKnowledgeLifecycleObserver
    ) {
        observerRegistryHolder
            .registry()
            .unregister(observer)
    }

    override fun reset() {
        observerRegistryHolder
            .reset()

        service =
            DefaultRuntimeKnowledgeLifecycleService(
                pipeline,
                observerProvider = observerProvider
            )
    }
}
