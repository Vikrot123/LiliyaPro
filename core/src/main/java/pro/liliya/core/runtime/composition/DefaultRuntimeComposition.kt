package pro.liliya.core.runtime.composition

import pro.liliya.core.runtime.lifecycle.DefaultRuntimeLifecycleRecorder
import pro.liliya.core.runtime.lifecycle.RuntimeLifecycleRecorder
import pro.liliya.core.runtime.observer.DefaultRuntimeObserverRegistry
import pro.liliya.core.runtime.observer.RuntimeObserverBridge
import pro.liliya.core.runtime.policy.DefaultRuntimeActionPolicyEvaluator
import pro.liliya.core.runtime.policy.RuntimeActionPolicyEvaluator

class DefaultRuntimeComposition : RuntimeComposition {

    private val observerRegistry =
        DefaultRuntimeObserverRegistry()

    private val observerBridge =
        RuntimeObserverBridge(observerRegistry)

    private val actionPolicyEvaluator =
        DefaultRuntimeActionPolicyEvaluator()

    private val lifecycleRecorder: RuntimeLifecycleRecorder =
        DefaultRuntimeLifecycleRecorder()

    override fun observerRegistry(): DefaultRuntimeObserverRegistry {
        return observerRegistry
    }

    override fun observerBridge(): RuntimeObserverBridge {
        return observerBridge
    }

    override fun actionPolicyEvaluator(): RuntimeActionPolicyEvaluator {
        return actionPolicyEvaluator
    }

    override fun lifecycleRecorder(): RuntimeLifecycleRecorder {
        return lifecycleRecorder
    }
}
