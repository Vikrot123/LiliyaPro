package pro.liliya.core.runtime.composition

import pro.liliya.core.runtime.lifecycle.RuntimeLifecycleRecorder
import pro.liliya.core.runtime.observer.RuntimeObserverBridge
import pro.liliya.core.runtime.observer.DefaultRuntimeObserverRegistry
import pro.liliya.core.runtime.policy.RuntimeActionPolicyEvaluator
import pro.liliya.core.runtime.health.RuntimeHealthProvider
import pro.liliya.core.runtime.health.RuntimeFailureTracker
import pro.liliya.core.runtime.health.RuntimeRecoveryTracker
import pro.liliya.core.runtime.health.RuntimeHealthReportProvider
import pro.liliya.core.runtime.status.RuntimeStatusProvider

interface RuntimeComposition {

    fun observerRegistry(): DefaultRuntimeObserverRegistry

    fun observerBridge(): RuntimeObserverBridge

    fun actionPolicyEvaluator(): RuntimeActionPolicyEvaluator

    fun lifecycleRecorder(): RuntimeLifecycleRecorder

    fun healthProvider(): RuntimeHealthProvider

    fun failureTracker(): RuntimeFailureTracker

    fun recoveryTracker(): RuntimeRecoveryTracker

    fun healthReportProvider(): RuntimeHealthReportProvider

    fun statusProvider(): RuntimeStatusProvider
}
