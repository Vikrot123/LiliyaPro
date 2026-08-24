package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertSame
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.observer.registry.RuntimeKnowledgeLifecycleObserverRegistry
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.observer.RuntimeKnowledgeLifecycleObserver
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.pipeline.RuntimeKnowledgeLifecyclePipeline
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.pipeline.RuntimeKnowledgeLifecyclePipelineResult
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.orchestrator.RuntimeKnowledgeLifecycleOrchestrationResult
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.executor.RuntimeKnowledgeLifecycleExecutionResult
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.DefaultRuntimeKnowledgeLifecycleService
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.RuntimeKnowledgeLifecycleServiceResult
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.provider.RuntimeKnowledgeLifecycleObserverProvider

class DefaultRuntimeKnowledgeLifecycleServiceProviderContractTest {

    private fun knowledge(): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = "provider knowledge",
            confidence = 0.9,
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = 1L
        )
    }

    @Test
    fun service_uses_observer_provider_for_notifications() {

        var received: RuntimeKnowledgeLifecycleServiceResult? = null

        val registry =
            object : RuntimeKnowledgeLifecycleObserverRegistry {

                override fun register(
                    observer: pro.liliya.core.runtime.intelligence.knowledge.lifecycle.observer.RuntimeKnowledgeLifecycleObserver
                ) {
                }

                override fun unregister(
                    observer: RuntimeKnowledgeLifecycleObserver
                ) {
                }

                override fun notify(
                    result: RuntimeKnowledgeLifecycleServiceResult
                ) {
                    received = result
                }
            }

        val provider =
            object : RuntimeKnowledgeLifecycleObserverProvider {

                override fun observerRegistry():
                    RuntimeKnowledgeLifecycleObserverRegistry {
                    return registry
                }
            }

        val service =
            DefaultRuntimeKnowledgeLifecycleService(
                pipeline =
                    object : RuntimeKnowledgeLifecyclePipeline {

                        override fun process(
                            knowledge: RuntimeKnowledge
                        ): RuntimeKnowledgeLifecyclePipelineResult {

                            return RuntimeKnowledgeLifecyclePipelineResult(
                                RuntimeKnowledgeLifecycleOrchestrationResult(
                                    RuntimeKnowledgeLifecycleExecutionResult(
                                        executed = true
                                    )
                                )
                            )
                        }
                    },
                observerProvider = provider
            )

        val result = service.processKnowledge(knowledge())

        assertSame(
            result,
            received
        )
    }
}
