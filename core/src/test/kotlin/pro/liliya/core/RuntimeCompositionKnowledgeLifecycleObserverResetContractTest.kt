package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertNull

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.observer.DefaultRuntimeKnowledgeLifecycleObserver

class RuntimeCompositionKnowledgeLifecycleObserverResetContractTest {

    private fun knowledge(): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = "observer reset test",
            confidence = 0.9,
            source = RuntimeKnowledgeSource.EXPERIENCE,
            createdAt = System.currentTimeMillis()
        )
    }

    @Test
    fun reset_should_not_destroy_new_delivery_path() {
        val composition =
            DefaultRuntimeComposition()

        val lifecycle =
            composition
                .knowledgeLifecycleComposition()

        val beforeResetObserver =
            DefaultRuntimeKnowledgeLifecycleObserver()

        lifecycle.registerLifecycleObserver(
            beforeResetObserver
        )

        lifecycle
            .lifecycleService()
            .processKnowledge(
                knowledge()
            )

        assertNotNull(
            beforeResetObserver.lastProcessedResult()
        )

        lifecycle.reset()

        val afterResetObserver =
            DefaultRuntimeKnowledgeLifecycleObserver()

        lifecycle.registerLifecycleObserver(
            afterResetObserver
        )

        lifecycle
            .lifecycleService()
            .processKnowledge(
                knowledge()
            )

        assertNotNull(
            afterResetObserver.lastProcessedResult()
        )
    }

    @Test
    fun separate_compositions_do_not_share_observers_after_reset() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        val observer =
            DefaultRuntimeKnowledgeLifecycleObserver()

        first
            .knowledgeLifecycleComposition()
            .registerLifecycleObserver(
                observer
            )

        second
            .knowledgeLifecycleComposition()
            .reset()

        second
            .knowledgeLifecycleComposition()
            .lifecycleService()
            .processKnowledge(
                knowledge()
            )

        assertNull(
            observer.lastProcessedResult()
        )
    }
    @Test
    fun reset_should_detach_old_observer_delivery() {
        val composition =
            DefaultRuntimeComposition()

        val lifecycle =
            composition
                .knowledgeLifecycleComposition()

        val oldObserver =
            DefaultRuntimeKnowledgeLifecycleObserver()

        lifecycle.registerLifecycleObserver(
            oldObserver
        )

        lifecycle.reset()

        lifecycle
            .lifecycleService()
            .processKnowledge(
                knowledge()
            )

        assertNull(
            oldObserver.lastProcessedResult()
        )
    }

    @Test
    fun reset_rebinds_service_to_new_observer_registry() {
        val composition = DefaultRuntimeComposition()
        val lifecycle = composition.knowledgeLifecycleComposition()

        val oldObserver = DefaultRuntimeKnowledgeLifecycleObserver()

        lifecycle.registerLifecycleObserver(oldObserver)

        lifecycle
            .lifecycleService()
            .processKnowledge(knowledge())

        val beforeResetResult = oldObserver.lastProcessedResult()

        assertNotNull(beforeResetResult)

        lifecycle.reset()

        val newObserver = DefaultRuntimeKnowledgeLifecycleObserver()

        lifecycle.registerLifecycleObserver(newObserver)

        lifecycle
            .lifecycleService()
            .processKnowledge(knowledge())

        val afterResetResult = newObserver.lastProcessedResult()

        assertNotNull(afterResetResult)
        assertNotSame(beforeResetResult, afterResetResult)
        assertSame(beforeResetResult, oldObserver.lastProcessedResult())
    }

}
