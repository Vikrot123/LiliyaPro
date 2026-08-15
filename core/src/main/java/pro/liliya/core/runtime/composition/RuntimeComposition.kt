package pro.liliya.core.runtime.composition

import pro.liliya.core.CoreDiagnosticSnapshot
import pro.liliya.core.CoreRuntimeStateHolder
import pro.liliya.core.CoreRuntimeState
import pro.liliya.core.CoreRuntimeDiagnosticsService
import pro.liliya.core.CoreRuntimeContext
import pro.liliya.core.module.ModuleRegistry
import pro.liliya.core.module.ModuleProviderHolder
import pro.liliya.core.module.ModuleProvider
import pro.liliya.core.module.ModuleManager
import pro.liliya.core.module.ModuleManagerHolder
import pro.liliya.core.module.ModuleState
import pro.liliya.core.runtime.lifecycle.RuntimeLifecycleRecorder
import pro.liliya.core.runtime.lifecycle.RuntimeLifecycleRecorderHolder
import pro.liliya.core.runtime.observer.RuntimeObserverBridge
import pro.liliya.core.runtime.observer.DefaultRuntimeObserverRegistry
import pro.liliya.core.runtime.policy.RuntimeActionPolicyEvaluator
import pro.liliya.core.runtime.capability.RuntimeCapabilityRegistry
import pro.liliya.core.runtime.capability.RuntimeCapabilityAuthorityEvaluator
import pro.liliya.core.runtime.capability.RuntimeCapabilityResolver
import pro.liliya.core.runtime.health.RuntimeHealthProvider
import pro.liliya.core.runtime.health.RuntimeFailureTracker
import pro.liliya.core.runtime.health.RuntimeRecoveryTracker
import pro.liliya.core.runtime.health.RuntimeHealthReportProvider
import pro.liliya.core.runtime.health.RuntimeHealthReport
import pro.liliya.core.runtime.status.RuntimeStatusSnapshot
import pro.liliya.core.runtime.health.RuntimeFailureHealthSnapshot
import pro.liliya.core.runtime.status.RuntimeStatusProvider
import pro.liliya.core.runtime.monitor.RuntimeMonitor
import pro.liliya.core.runtime.telemetry.RuntimeTelemetryObserver
import pro.liliya.core.runtime.control.RuntimeControl
import pro.liliya.core.runtime.history.RuntimeCommandHistoryProvider
import pro.liliya.core.runtime.audit.RuntimeActionAuditProvider
import pro.liliya.core.runtime.dispatcher.RuntimeActionDispatcher
import pro.liliya.core.runtime.RuntimeBridgeStateHolder
import pro.liliya.core.runtime.RuntimeServiceBootstrap
import pro.liliya.core.runtime.RuntimeServiceBootstrapHolder
import pro.liliya.core.runtime.RuntimeServiceProvider
import pro.liliya.core.runtime.RuntimeServiceProviderHolder
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.RuntimeServiceFailure
import pro.liliya.core.runtime.RuntimeServiceHealth
import pro.liliya.core.runtime.RuntimeRecoverySnapshot
import pro.liliya.core.runtime.health.RuntimeRecoverySnapshot as HealthRecoverySnapshot
import pro.liliya.core.runtime.health.RuntimeHealthSnapshot
import pro.liliya.core.runtime.telemetry.RuntimeTelemetrySnapshot

interface RuntimeComposition {

    fun context(): CoreRuntimeContext

    fun createModuleRegistry(): ModuleRegistry
    fun moduleManagerHolder(): ModuleManagerHolder
    fun moduleManager(): ModuleManager?
    fun setModuleManager(manager: ModuleManager)
    fun clearModuleManager()

    fun runtimeStateHolder(): CoreRuntimeStateHolder

    fun runtimeState(): CoreRuntimeState
    fun setRuntimeState(state: CoreRuntimeState)
    fun failureReason(): String?
    fun setFailureReason(reason: String?)

    fun markRuntimeFailed(reason: String)

    fun moduleStates(): Map<String, ModuleState>
    fun setModuleStates(states: Map<String, ModuleState>)

    fun runtimeBridgeStateHolder(): RuntimeBridgeStateHolder
    fun isRuntimeObserverBridgeInstalled(): Boolean
    fun markRuntimeObserverBridgeInstalled()
    fun markModuleEventBridgeInstalled()
    fun resetRuntimeBridgeState()


    fun createModuleManager(
        registry: ModuleRegistry,
        provider: pro.liliya.core.module.ModuleProvider
    ): ModuleManager

    fun createModuleRuntime(): ModuleManager

    fun startRuntimeComponents(): ModuleManager

    fun startRuntimeLifecycle(): ModuleManager

    fun stopRuntimeLifecycle()

    fun startModuleRuntime(
        manager: ModuleManager
    )
    fun stopModuleRuntime(
        manager: ModuleManager
    )
    fun clearModuleRuntime()
    fun moduleProvider(): pro.liliya.core.module.ModuleProvider
    fun moduleProviderHolder(): ModuleProviderHolder

    fun setModuleProvider(
        provider: ModuleProvider
    )

    fun resetModuleProvider()
    fun diagnosticsService(): CoreRuntimeDiagnosticsService

    fun createDiagnosticSnapshot(): CoreDiagnosticSnapshot

    fun observerRegistry(): DefaultRuntimeObserverRegistry

    fun observerBridge(): RuntimeObserverBridge

    fun actionPolicyEvaluator(): RuntimeActionPolicyEvaluator

    fun capabilityRegistry(): RuntimeCapabilityRegistry

    fun capabilityAuthorityEvaluator(): RuntimeCapabilityAuthorityEvaluator

    fun capabilityResolver(): RuntimeCapabilityResolver

    fun lifecycleRecorder(): RuntimeLifecycleRecorder

    fun lifecycleRecorderHolder(): RuntimeLifecycleRecorderHolder

    fun recordRuntimeStarted()
    fun recordRuntimeStopped()
    fun recordRuntimeFailure(reason: String?)

    fun healthProvider(): RuntimeHealthProvider
    fun createHealthSnapshot(
        state: CoreRuntimeState,
        telemetry: RuntimeTelemetrySnapshot,
        failureReason: String?
    ): RuntimeHealthSnapshot

    fun runtimeHealthSnapshot(): RuntimeHealthSnapshot


    fun failureTracker(): RuntimeFailureTracker

    fun recoveryTracker(): RuntimeRecoveryTracker

    fun recordRuntimeFailure(
        reason: String,
        module: String
    )

    fun resetRuntimeHealth()

    fun markRuntimeRecovered()

    fun healthReportProvider(): RuntimeHealthReportProvider
    fun createHealthReport(
        state: CoreRuntimeState,
        telemetry: RuntimeTelemetrySnapshot,
        failure: RuntimeFailureHealthSnapshot,
        recovery: HealthRecoverySnapshot
    ): RuntimeHealthReport

    fun runtimeHealthReport(): RuntimeHealthReport


    fun statusProvider(): RuntimeStatusProvider

    fun createRuntimeStatus(
        report: RuntimeHealthReport
    ): RuntimeStatusSnapshot

    fun runtimeStatusSnapshot(): RuntimeStatusSnapshot

    fun runtimeMonitor(): RuntimeMonitor

    fun telemetryObserver(): RuntimeTelemetryObserver

    fun registerRuntimeControls()

    fun commandHistory(): RuntimeCommandHistoryProvider

    fun actionAuditProvider(): RuntimeActionAuditProvider

    fun registerRuntimeActionHandlers()

    fun actionDispatcher(): RuntimeActionDispatcher

    fun defaultRuntimeControl(): RuntimeControl
    fun serviceProvider(): RuntimeServiceProvider
    fun runtimeServiceProviderHolder(): RuntimeServiceProviderHolder
    fun setRuntimeServiceProvider(provider: RuntimeServiceProvider)
    fun runtimeServiceProvider(): RuntimeServiceProvider
    fun resetRuntimeServiceProvider()

    fun configureRuntimeServiceProvider(
        provider: RuntimeServiceProvider
    )

    fun resetRuntimeServiceConfiguration()

    fun serviceBootstrap(): RuntimeServiceBootstrap
    fun runtimeServiceBootstrapHolder(): RuntimeServiceBootstrapHolder
    fun setRuntimeServiceBootstrap(bootstrap: RuntimeServiceBootstrap)
    fun runtimeServiceBootstrap(): RuntimeServiceBootstrap


    fun registerRuntimeService(
        service: RuntimeService
    )

    fun runtimeServiceStates(): Map<String, RuntimeServiceState>
    fun runtimeServiceFailures(): List<RuntimeServiceFailure>
    fun runtimeServiceHealth(): Map<String, RuntimeServiceHealth>
    fun runtimeServiceRecoverySnapshot(): RuntimeRecoverySnapshot?

    fun startRuntimeServices()
    fun stopRuntimeServices()

    fun createServiceBootstrap(
        provider: RuntimeServiceProvider
    ): RuntimeServiceBootstrap
}
