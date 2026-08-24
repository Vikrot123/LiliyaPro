package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.observer.RuntimeKnowledgeLifecycleObserver
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.observer.DefaultRuntimeKnowledgeLifecycleObserver

class RuntimeCompositionKnowledgeLifecycleObserverDuplicateDeliveryContractTest {

    private fun knowledge(): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = "observer duplicate delivery test",
            confidence = 0.9,
            source = RuntimeKnowledgeSource.EXPERIENCE,
            createdAt = System.currentTimeMillis()
        )
    }

    @Test
    fun duplicate_observer_registration_should_deliver_once() {
        val composition =
            DefaultRuntimeComposition()

        val lifecycle =
            composition
                .knowledgeLifecycleComposition()

        val observer =
            CountingObserver()

        lifecycle.registerLifecycleObserver(observer)
        lifecycle.registerLifecycleObserver(observer)

        lifecycle
            .lifecycleService()
            .processKnowledge(
                knowledge()
            )

        assertEquals(
            1,
            observer.deliveryCount
        )
    }

    @Test
    fun different_observers_should_each_receive_once() {
        val composition =
            DefaultRuntimeComposition()

        val lifecycle =
            composition
                .knowledgeLifecycleComposition()

        val first =
            CountingObserver()

        val second =
            CountingObserver()

        lifecycle.registerLifecycleObserver(first)
        lifecycle.registerLifecycleObserver(second)

        lifecycle
            .lifecycleService()
            .processKnowledge(
                knowledge()
            )

        assertEquals(
            1,
            first.deliveryCount
        )

        assertEquals(
            1,
            second.deliveryCount
        )
    }


    private class CountingObserver :
        RuntimeKnowledgeLifecycleObserver {

        var deliveryCount = 0

        override fun onProcessed(
            result: pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.RuntimeKnowledgeLifecycleServiceResult
        ) {
            deliveryCount++
        }
    }
}
