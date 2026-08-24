package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.observer.RuntimeKnowledgeLifecycleObserver
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.RuntimeKnowledgeLifecycleServiceResult

class RuntimeCompositionKnowledgeLifecycleObserverUninstallContractTest {

    private fun knowledge(): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = "observer uninstall test",
            confidence = 0.9,
            source = RuntimeKnowledgeSource.EXPERIENCE,
            createdAt = System.currentTimeMillis()
        )
    }

    @Test
    fun unregistered_observer_should_stop_receiving_results() {
        val composition = DefaultRuntimeComposition()

        val lifecycle =
            composition
                .knowledgeLifecycleComposition()

        val observer = CountingObserver()

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

        lifecycle.unregisterLifecycleObserver(observer)

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

    private class CountingObserver :
        RuntimeKnowledgeLifecycleObserver {

        var deliveryCount = 0

        override fun onProcessed(
            result: RuntimeKnowledgeLifecycleServiceResult
        ) {
            deliveryCount++
        }
    }
}
