package pro.liliya.core.runtime.composition

import pro.liliya.core.CoreRuntimeDiagnosticsService
import pro.liliya.core.CoreRuntimeContext
import pro.liliya.core.module.ModuleProvider
import pro.liliya.core.module.CoreModuleProvider
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
import pro.liliya.core.runtime.telemetry.RuntimeTelemetryObserver
import pro.liliya.core.runtime.control.RuntimeControlRegistry
import pro.liliya.core.runtime.action.RuntimeActionExecutor

import pro.liliya.core.runtime.dispatcher.HealthRuntimeActionHandler

import pro.liliya.core.runtime.control.DefaultRuntimeControl

import pro.liliya.core.runtime.control.RuntimeControl

import pro.liliya.core.runtime.history.RuntimeCommandHistoryProvider
import pro.liliya.core.runtime.audit.RuntimeActionAuditProvider
import pro.liliya.core.runtime.dispatcher.RuntimeActionHandlerRegistry
import pro.liliya.core.runtime.dispatcher.RuntimeActionDispatcher
import pro.liliya.core.runtime.RuntimeServiceBootstrap
import pro.liliya.core.runtime.RuntimeServiceProvider
import pro.liliya.core.runtime.CoreRuntimeServiceProvider
import pro.liliya.core.runtime.RuntimeServiceRegistry

class DefaultRuntimeComposition : RuntimeComposition {

    private val context = CoreRuntimeContext()


    private val moduleProvider: ModuleProvider =
        CoreModuleProvider()


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

    private val telemetryObserver =
        RuntimeTelemetryObserver()

    private val controlRegistry =
        RuntimeControlRegistry()

    private val commandHistory =
        RuntimeCommandHistoryProvider()

    private val actionAuditProvider =
        RuntimeActionAuditProvider()

    private val actionHandlerRegistry =
        RuntimeActionHandlerRegistry()

    private val actionDispatcher =
        RuntimeActionDispatcher(
            actionHandlerRegistry,
            actionAuditProvider,
            actionPolicyEvaluator
        )

    private val defaultRuntimeControl =
        DefaultRuntimeControl()

    private val healthRuntimeActionHandler =
        HealthRuntimeActionHandler(
            RuntimeActionExecutor()
        )

    private val serviceProvider =
        CoreRuntimeServiceProvider()

    private val serviceBootstrap =
        createServiceBootstrap(
            serviceProvider
        )

    override fun context(): CoreRuntimeContext {
        return context
    }

    override fun moduleProvider(): ModuleProvider {
        return moduleProvider
    }

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

    override fun telemetryObserver(): RuntimeTelemetryObserver {
        return telemetryObserver
    }

    override fun registerRuntimeControls() {
        controlRegistry.register(defaultRuntimeControl)
    }

    override fun commandHistory(): RuntimeCommandHistoryProvider {
        return commandHistory
    }

    override fun actionAuditProvider(): RuntimeActionAuditProvider {
        return actionAuditProvider
    }

    override fun registerRuntimeActionHandlers() {
        actionHandlerRegistry.register(healthRuntimeActionHandler)
    }



    override fun actionDispatcher(): RuntimeActionDispatcher {
        return actionDispatcher
    }

    override fun defaultRuntimeControl(): RuntimeControl {
        return defaultRuntimeControl
    }



    override fun serviceProvider(): RuntimeServiceProvider {
        return serviceProvider
    }

    override fun serviceBootstrap(): RuntimeServiceBootstrap {
        return serviceBootstrap
    }

    override fun createServiceBootstrap(
        provider: RuntimeServiceProvider
    ): RuntimeServiceBootstrap {
        return RuntimeServiceBootstrap(
            provider,
            RuntimeServiceRegistry()
        )
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
