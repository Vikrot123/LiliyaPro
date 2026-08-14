package pro.liliya.core

import pro.liliya.core.logging.LogConfig
import pro.liliya.core.logging.Logger
import pro.liliya.core.logging.LoggerFactory
import pro.liliya.core.module.ModuleManager
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceRegistry
import pro.liliya.core.runtime.RuntimeServiceProvider
import pro.liliya.core.runtime.RuntimeServiceBootstrap
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.monitor.RuntimeMonitor
import pro.liliya.core.runtime.lifecycle.DefaultRuntimeLifecycleRecorder
import pro.liliya.core.runtime.lifecycle.RuntimeLifecycleRecorder
import pro.liliya.core.runtime.lifecycle.RuntimeLifecycleEvent
import pro.liliya.core.runtime.observer.DefaultRuntimeObserverRegistry
import pro.liliya.core.runtime.observer.RuntimeObserver
import pro.liliya.core.runtime.observer.RuntimeObserverBridge
import pro.liliya.core.runtime.telemetry.RuntimeTelemetryObserver
import pro.liliya.core.runtime.health.RuntimeHealthProvider
import pro.liliya.core.runtime.health.RuntimeHealthSnapshot
import pro.liliya.core.runtime.health.RuntimeFailureTracker
import pro.liliya.core.runtime.health.RuntimeRecoveryTracker
import pro.liliya.core.runtime.health.RuntimeRecoverySnapshot
import pro.liliya.core.runtime.health.RuntimeHealthReport
import pro.liliya.core.runtime.health.RuntimeHealthReportProvider
import pro.liliya.core.runtime.status.RuntimeStatusProvider
import pro.liliya.core.runtime.status.RuntimeStatusSnapshot
import pro.liliya.core.runtime.control.RuntimeControlResult
import pro.liliya.core.runtime.history.RuntimeCommandRecord
import pro.liliya.core.runtime.action.RuntimeActionResult
import pro.liliya.core.runtime.audit.RuntimeActionAuditProvider
import pro.liliya.core.runtime.audit.RuntimeActionAuditRecord
import pro.liliya.core.runtime.policy.DefaultRuntimeActionPolicyEvaluator
import pro.liliya.core.runtime.policy.RuntimeActionPolicyEvaluator
import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.dispatcher.RuntimeActionDispatcher
import pro.liliya.core.runtime.history.RuntimeCommandHistoryProvider
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.health.RuntimeFailureHealthSnapshot
import pro.liliya.core.runtime.telemetry.RuntimeTelemetrySnapshot

object CoreRuntime {

    private val runtimeComposition =
        DefaultRuntimeComposition()

    private val context = runtimeComposition.context()

    private val runtimeObserverRegistry =
        runtimeComposition.observerRegistry()

    private val runtimeObserverBridge =
        runtimeComposition.observerBridge()

    private val runtimeTelemetryObserver =
        runtimeComposition.telemetryObserver()

    private val runtimeHealthProvider =
        runtimeComposition.healthProvider()

    private val runtimeFailureTracker =
        runtimeComposition.failureTracker()

    private val runtimeRecoveryTracker =
        runtimeComposition.recoveryTracker()
    private val runtimeHealthReportProvider =
        runtimeComposition.healthReportProvider()

    private val runtimeStatusProvider =
        runtimeComposition.statusProvider()

    private val runtimeCommandHistory =
        runtimeComposition.commandHistory()

    private val runtimeActionAuditProvider =
        runtimeComposition.actionAuditProvider()

    private val runtimeActionPolicyEvaluator =
        runtimeComposition.actionPolicyEvaluator()

    private val runtimeActionDispatcher =
        runtimeComposition.actionDispatcher()

    private var runtimeObserverBridgeInstalled = false



    private var moduleManager: ModuleManager? = null

    private val moduleProviderHolder =
        runtimeComposition.moduleProviderHolder()

    private var runtimeState = CoreRuntimeState.STOPPED

    private var lastFailureReason: String? = null

    private var lastModuleStates: Map<String, pro.liliya.core.module.ModuleState> =
        emptyMap()

    private var moduleEventBridgeInstalled = false

private var runtimeServiceBootstrap =
        runtimeComposition.serviceBootstrap()

private val runtimeServiceProviderHolder =
        runtimeComposition.runtimeServiceProviderHolder()

    private val diagnosticEventBus = context.diagnosticEventBus

    private val runtimeDiagnosticsService =
        runtimeComposition.diagnosticsService()

    private val runtimeLifecycleRecorder: RuntimeLifecycleRecorder =
        runtimeComposition.lifecycleRecorder()

    private val runtimeMonitor: RuntimeMonitor =
        runtimeComposition.runtimeMonitor(
            runtimeDiagnosticsService,
            runtimeLifecycleRecorder
        )

    private val logger: Logger
        get() = LoggerFactory.create(
            module = "CORE",
            component = "CoreRuntime",
            method = "lifecycle"
        )

    fun state(): CoreRuntimeState {
        return runtimeState
    }

    fun snapshot(): CoreDiagnosticSnapshot {
        return CoreDiagnosticSnapshot(
            runtimeState = runtimeState,
            moduleStates = moduleManager?.getModuleStates()
                ?: lastModuleStates,
            runtimeServiceStates = runtimeServiceBootstrap.getStates(),
            runtimeServiceFailures = runtimeServiceBootstrap.getFailures(),
            runtimeServiceHealth = runtimeServiceBootstrap.getHealth(),
            runtimeRecoverySnapshot = runtimeServiceBootstrap.getRecoverySnapshot(),
            runtimeStatusSnapshot = getRuntimeStatusSnapshot(),
            failureReason = lastFailureReason
        )
    }

    fun diagnostics(): CoreRuntimeDiagnosticsSnapshot {
        return runtimeDiagnosticsService.snapshot()
    }

    fun monitor(): pro.liliya.core.runtime.monitor.RuntimeMonitorSnapshot {
        return runtimeMonitor.snapshot()
    }

    fun registerRuntimeService(service: RuntimeService) {
        runtimeServiceBootstrap.register(service)
    }

    internal fun setRuntimeServiceProvider(
        provider: RuntimeServiceProvider
    ) {
        runtimeServiceProviderHolder.set(provider)
        runtimeServiceBootstrap =
            runtimeComposition.createServiceBootstrap(
                runtimeServiceProviderHolder.get()
            )
    }

    internal fun resetRuntimeServiceProvider() {
        runtimeServiceProviderHolder.reset()

        runtimeServiceBootstrap =
            runtimeComposition.createServiceBootstrap(
                runtimeServiceProviderHolder.get()
            )
    }



    fun registerDiagnosticListener(
        listener: CoreDiagnosticEventListener
    ) {
        diagnosticEventBus.register(listener)
    }

    fun unregisterDiagnosticListener(
        listener: CoreDiagnosticEventListener
    ) {
        diagnosticEventBus.unregister(listener)
    }

    fun registerRuntimeObserver(observer: RuntimeObserver) {
        runtimeObserverRegistry.subscribe(observer)
    }

    fun unregisterRuntimeObserver(observer: RuntimeObserver) {
        runtimeObserverRegistry.unsubscribe(observer)
    }

    fun getRuntimeTelemetrySnapshot():
        RuntimeTelemetrySnapshot {
        return runtimeTelemetryObserver.snapshot()
    }

    fun getRuntimeHealthSnapshot():
            RuntimeHealthSnapshot {
        return runtimeHealthProvider.createSnapshot(
            state = runtimeState,
            telemetry = runtimeTelemetryObserver.snapshot(),
            failureReason = lastFailureReason
        )
    }

    fun getRuntimeFailureHealthSnapshot():
        RuntimeFailureHealthSnapshot {
        return runtimeFailureTracker.snapshot()
    }
    
    fun getRuntimeRecoverySnapshot():
        RuntimeRecoverySnapshot {
        return runtimeRecoveryTracker.snapshot()
    }
    
    fun getRuntimeHealthReport(): RuntimeHealthReport {
        return runtimeHealthReportProvider.createReport(
            state = runtimeState,
            telemetry = runtimeTelemetryObserver.snapshot(),
            failure = runtimeFailureTracker.snapshot(),
            recovery = runtimeRecoveryTracker.snapshot()
        )
    }
    
    fun getRuntimeStatusSnapshot(): RuntimeStatusSnapshot {
        return runtimeStatusProvider.createStatus(
            report = getRuntimeHealthReport()
        )
    }



    fun getRuntimeCommandHistory(): List<RuntimeCommandRecord> {
        return runtimeCommandHistory.snapshot()
    }

    fun getRuntimeActionAudit(): List<RuntimeActionAuditRecord> {
        return runtimeActionAuditProvider.snapshot()
    }


    fun getRuntimeState(): CoreRuntimeState {
        return runtimeState
    }


    fun dispatchRuntimeAction(
        request: RuntimeActionRequest
    ): RuntimeActionResult {
        return runtimeActionDispatcher.dispatch(request)
    }

    fun executeRuntimeCommand(
        command: RuntimeCommand
    ): RuntimeControlResult {
        val control = runtimeComposition.defaultRuntimeControl()

        val result = control?.execute(command)
            ?: RuntimeControlResult(
                command = command,
                success = false,
                previousState = runtimeState,
                currentState = runtimeState,
                status = getRuntimeStatusSnapshot(),
                message = "Runtime control is not available"
            )

        runtimeCommandHistory.record(
            RuntimeCommandRecord(
                command = result.command,
                success = result.success,
                previousState = result.previousState,
                currentState = result.currentState,
                message = result.message
            )
        )

        return result
    }





    private fun installRuntimeObserverBridge() {
        if (runtimeObserverBridgeInstalled) {
            return
        }

        runtimeObserverBridge.install()

        runtimeObserverRegistry.subscribe(
            runtimeTelemetryObserver
        )
        runtimeObserverBridgeInstalled = true
    }

    private fun installModuleEventBridge() {

        if (ModuleEventBus.hasListeners()) {
            return
        }

        ModuleEventBus.subscribe { event ->
            if (event is ModuleEvent.Failed) {
                runtimeFailureTracker.recordFailure(
                    reason = "${event.phase}: ${event.reason}",
                    module = event.moduleName
                )

                RuntimeEventBus.publish(
                    RuntimeEvent.ModuleFailed(
                        moduleName = event.moduleName,
                        reason = "${event.moduleName}: ${event.phase}: ${event.reason}"
                    )
                )
            }
        }

        moduleEventBridgeInstalled = true
    }

    fun start() {
        if (runtimeState == CoreRuntimeState.RUNNING ||
            runtimeState == CoreRuntimeState.STARTING
        ) {
            return
        }

        runtimeState = CoreRuntimeState.STARTING

        runtimeTelemetryObserver.reset()
        runtimeFailureTracker.clear()

        installRuntimeObserverBridge()
        installModuleEventBridge()

        RuntimeEventBus.publish(
            RuntimeEvent.SystemStart
        )

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeStarting
        )

        logger.info(
            LogConfig.SYSTEM_START,
            "Core runtime starting"
        )

        val manager = runtimeComposition.createModuleManager(
                registry = runtimeComposition.createModuleRegistry(),
                provider = moduleProviderHolder.get()
            )

            try {
            moduleManager = manager

            manager.loadModules()
            manager.startModules()

            runtimeServiceBootstrap.start()

            runtimeComposition.registerRuntimeControls()
            runtimeComposition.registerRuntimeActionHandlers()
            runtimeState = CoreRuntimeState.RUNNING

            runtimeLifecycleRecorder.record(
                RuntimeLifecycleEvent.STARTED
            )

            diagnosticEventBus.publish(
                CoreDiagnosticEvent(
                    type = CoreDiagnosticEventType.RUNTIME_STARTED,
                    snapshot = snapshot()
                )
            )

            runtimeRecoveryTracker.markRecovered()

            RuntimeEventBus.publish(
                RuntimeEvent.RuntimeReady
            )

            logger.info(
                LogConfig.MODULE_READY,
                "Core runtime ready"
            )
        } catch (error: Exception) {
            logger.info(
                LogConfig.ERROR_CAUGHT,
                "Core runtime startup failed: ${error.message}"
            )

            lastModuleStates = manager.getModuleStates()

            try {
                manager.stopModules()
            } catch (_: Exception) {
            }

            moduleManager = null
            lastFailureReason = error.message ?: "unknown"
            runtimeState = CoreRuntimeState.FAILED

            runtimeLifecycleRecorder.record(
                RuntimeLifecycleEvent.FAILED,
                lastFailureReason
            )

            diagnosticEventBus.publish(
                CoreDiagnosticEvent(
                    type = CoreDiagnosticEventType.RUNTIME_FAILED,
                    snapshot = snapshot()
                )
            )

            RuntimeEventBus.publish(
                RuntimeEvent.RuntimeFailed(
                    error.message ?: "unknown"
                )
            )

            throw error
        }
    }

    internal fun setModuleProvider(
        provider: pro.liliya.core.module.ModuleProvider
    ) {
        moduleProviderHolder.set(provider)
    }

    internal fun resetModuleProvider() {
        moduleProviderHolder.reset()
    }

    fun stop() {
        if (runtimeState == CoreRuntimeState.STOPPED) {
            return
        }

        runtimeServiceBootstrap.stop()

        moduleManager?.stopModules()

        moduleManager = null
          runtimeState = CoreRuntimeState.STOPPED

        runtimeLifecycleRecorder.record(
            RuntimeLifecycleEvent.STOPPED
        )
          lastFailureReason = null
          lastModuleStates = emptyMap()

        diagnosticEventBus.publish(
            CoreDiagnosticEvent(
                type = CoreDiagnosticEventType.RUNTIME_STOPPED,
                snapshot = snapshot()
            )
        )

        RuntimeEventBus.publish(
            RuntimeEvent.SystemStop
        )

        runtimeObserverBridge.uninstall()

        runtimeObserverRegistry.unsubscribe(
            runtimeTelemetryObserver
        )

        runtimeObserverBridgeInstalled = false

        logger.info(
            LogConfig.SYSTEM_STOP,
            "Core runtime stopped"
        )
    }
}
