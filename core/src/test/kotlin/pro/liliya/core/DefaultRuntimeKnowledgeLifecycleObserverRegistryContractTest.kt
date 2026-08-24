package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.observer.RuntimeKnowledgeLifecycleObserver
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.observer.registry.DefaultRuntimeKnowledgeLifecycleObserverRegistry
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.RuntimeKnowledgeLifecycleProcessingStatus
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.RuntimeKnowledgeLifecycleServiceResult

class DefaultRuntimeKnowledgeLifecycleObserverRegistryContractTest {

    @Test
    fun registry_notifies_registered_observer() {

        val registry =
            DefaultRuntimeKnowledgeLifecycleObserverRegistry()

        var received:
            RuntimeKnowledgeLifecycleServiceResult? = null

        val observer =
            object : RuntimeKnowledgeLifecycleObserver {

                override fun onProcessed(
                    result: RuntimeKnowledgeLifecycleServiceResult
                ) {
                    received = result
                }
            }

        val result =
            RuntimeKnowledgeLifecycleServiceResult(
                status =
                    RuntimeKnowledgeLifecycleProcessingStatus.EXECUTED,
                pipelineResult = null,
                error = null
            )

        registry.register(observer)

        registry.notify(result)

        assertEquals(
            result,
            received
        )
    }

    @Test
    fun registry_prevents_duplicate_observer_notification() {

        val registry =
            DefaultRuntimeKnowledgeLifecycleObserverRegistry()

        var count = 0

        val observer =
            object : RuntimeKnowledgeLifecycleObserver {

                override fun onProcessed(
                    result: RuntimeKnowledgeLifecycleServiceResult
                ) {
                    count++
                }
            }

        val result =
            RuntimeKnowledgeLifecycleServiceResult(
                status =
                    RuntimeKnowledgeLifecycleProcessingStatus.EXECUTED,
                pipelineResult = null,
                error = null
            )

        registry.register(observer)
        registry.register(observer)

        registry.notify(result)

        assertEquals(
            1,
            count
        )
    }

}
