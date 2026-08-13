package pro.liliya.core.runtime.composition

import pro.liliya.core.CoreRuntimeDiagnosticsService
import pro.liliya.core.runtime.lifecycle.DefaultRuntimeLifecycleRecorder
import pro.liliya.core.runtime.lifecycle.RuntimeLifecycleRecorder
import pro.liliya.core.runtime.observer.DefaultRuntimeObserverRegistry
import pro.liliya.core.runtime.observer.RuntimeObserverBridge
import pro.liliya.core.runtime.policy.DefaultRuntimeActionPolicyEvaluator
import pro.liliya.core.runtime.policy.RuntimeActionPolicyEvaluator
import pro.liliya.core.runtime.health.RuntimeHealthProvider
import pro.liliya.core.runtime.health.RuntimeFailureTracker
import pro.liliya.core.runtime.health.RuntimeRecoveryTracker
import pro.liliya.core.runtime.health.RuntimeHealthReportProvider
import pro.liliya.core.runtime.status.RuntimeStatusProvider
import pro.liliya.core.runtime.monitor.DefaultRuntimeMonitor
import pro.liliya.core.runtime.monitor.RuntimeMonitor

class DefaultRuntimeComposition : RuntimeComposition {

    private val observerRegistry =
        DefaultRuntimeObserverRegistry()

    private val observerBridge =
        RuntimeObserverBridge(observerRegistry)

    private val actionPolicyEvaluator =
        DefaultRuntimeActionPolicyEvaluator()

    private val lifecycleRecorder: RuntimeLifecycleRecorder =
        DefaultRuntimeLifecycleRecorder()

    private val healthProvider =
        RuntimeHealthProvider()

    private val failureTracker =
        RuntimeFailureTracker()

    private val recoveryTracker =
        RuntimeRecoveryTracker()

    private val healthReportProvider =
        RuntimeHealthReportProvider()

    private val statusProvider =
        RuntimeStatusProvider()

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

    override fun healthProvider(): RuntimeHealthProvider {
        return healthProvider
    }

    override fun failureTracker(): RuntimeFailureTracker {
        return failureTracker
    }

    override fun recoveryTracker(): RuntimeRecoveryTracker {
        return recoveryTracker
    }

    override fun healthReportProvider(): RuntimeHealthReportProvider {
        return healthReportProvider
    }

    override fun statusProvider(): RuntimeStatusProvider {
        return statusProvider
    }

    
    override fun runtimeMonitor(
        diagnosticsService: CoreRuntimeDiagnosticsService,
        lifecycleRecorder: RuntimeLifecycleRecorder
    ): RuntimeMonitor {
        return DefaultRuntimeMonitor(
            diagnosticsService,
            lifecycleRecorder
        )
    }
}
