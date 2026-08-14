package pro.liliya.core.runtime.composition

import pro.liliya.core.CoreRuntimeDiagnosticsService
import pro.liliya.core.runtime.lifecycle.RuntimeLifecycleRecorder
import pro.liliya.core.runtime.observer.RuntimeObserverBridge
import pro.liliya.core.runtime.observer.DefaultRuntimeObserverRegistry
import pro.liliya.core.runtime.policy.RuntimeActionPolicyEvaluator
import pro.liliya.core.runtime.health.RuntimeHealthProvider
import pro.liliya.core.runtime.health.RuntimeFailureTracker
import pro.liliya.core.runtime.health.RuntimeRecoveryTracker
import pro.liliya.core.runtime.health.RuntimeHealthReportProvider
import pro.liliya.core.runtime.status.RuntimeStatusProvider
import pro.liliya.core.runtime.monitor.RuntimeMonitor
import pro.liliya.core.runtime.telemetry.RuntimeTelemetryObserver
import pro.liliya.core.runtime.control.RuntimeControl
import pro.liliya.core.runtime.history.RuntimeCommandHistoryProvider
import pro.liliya.core.runtime.audit.RuntimeActionAuditProvider
import pro.liliya.core.runtime.dispatcher.RuntimeActionDispatcher
import pro.liliya.core.runtime.RuntimeServiceBootstrap
import pro.liliya.core.runtime.RuntimeServiceProvider

interface RuntimeComposition {

    fun moduleProvider(): pro.liliya.core.module.ModuleProvider
    fun observerRegistry(): DefaultRuntimeObserverRegistry

    fun observerBridge(): RuntimeObserverBridge

    fun actionPolicyEvaluator(): RuntimeActionPolicyEvaluator

    fun lifecycleRecorder(): RuntimeLifecycleRecorder

    fun healthProvider(): RuntimeHealthProvider

    fun failureTracker(): RuntimeFailureTracker

    fun recoveryTracker(): RuntimeRecoveryTracker

    fun healthReportProvider(): RuntimeHealthReportProvider

    fun statusProvider(): RuntimeStatusProvider

    fun runtimeMonitor(
        diagnosticsService: CoreRuntimeDiagnosticsService,
        lifecycleRecorder: RuntimeLifecycleRecorder
    ): RuntimeMonitor

    fun telemetryObserver(): RuntimeTelemetryObserver

    fun registerRuntimeControls()

    fun commandHistory(): RuntimeCommandHistoryProvider

    fun actionAuditProvider(): RuntimeActionAuditProvider

    fun registerRuntimeActionHandlers()

    fun actionDispatcher(): RuntimeActionDispatcher

    fun defaultRuntimeControl(): RuntimeControl
    fun serviceProvider(): RuntimeServiceProvider
    fun serviceBootstrap(): RuntimeServiceBootstrap

    fun createServiceBootstrap(
        provider: RuntimeServiceProvider
    ): RuntimeServiceBootstrap
}
