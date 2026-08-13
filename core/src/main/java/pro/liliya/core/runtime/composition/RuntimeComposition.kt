package pro.liliya.core.runtime.composition

import pro.liliya.core.runtime.lifecycle.RuntimeLifecycleRecorder
import pro.liliya.core.runtime.observer.RuntimeObserverBridge
import pro.liliya.core.runtime.observer.DefaultRuntimeObserverRegistry
import pro.liliya.core.runtime.policy.RuntimeActionPolicyEvaluator

interface RuntimeComposition {

    fun observerRegistry(): DefaultRuntimeObserverRegistry

    fun observerBridge(): RuntimeObserverBridge

    fun actionPolicyEvaluator(): RuntimeActionPolicyEvaluator

    fun lifecycleRecorder(): RuntimeLifecycleRecorder
}
