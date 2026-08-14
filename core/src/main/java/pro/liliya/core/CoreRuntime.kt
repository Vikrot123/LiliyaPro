package pro.liliya.core

import pro.liliya.core.logging.LogConfig
import pro.liliya.core.logging.Logger
import pro.liliya.core.logging.LoggerFactory
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceRegistry
import pro.liliya.core.runtime.RuntimeServiceProvider
import pro.liliya.core.runtime.RuntimeServiceBootstrap
import pro.liliya.core.runtime.composition.RuntimeCompositionFactory
import pro.liliya.core.runtime.monitor.RuntimeMonitor
import pro.liliya.core.runtime.lifecycle.RuntimeLifecycleRecorder
import pro.liliya.core.runtime.lifecycle.RuntimeLifecycleEvent
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
import pro.liliya.core.runtime.policy.RuntimeActionPolicyEvaluator
import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.dispatcher.RuntimeActionDispatcher
import pro.liliya.core.runtime.history.RuntimeCommandHistoryProvider
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.health.RuntimeFailureHealthSnapshot
import pro.liliya.core.runtime.telemetry.RuntimeTelemetrySnapshot

object CoreRuntime {

    private val runtimeComposition =
        RuntimeCompositionFactory.create()

    private val context = runtimeComposition.context()

    private val runtimeObserverBridge =
        runtimeComposition.observerBridge()

    private val runtimeTelemetryObserver =
        runtimeComposition.telemetryObserver()

    private val runtimeFailureTracker =
        runtimeComposition.failureTracker()

    private val runtimeRecoveryTracker =
        runtimeComposition.recoveryTracker()
    private val runtimeCommandHistory =
        runtimeComposition.commandHistory()

    private val runtimeActionPolicyEvaluator =
        runtimeComposition.actionPolicyEvaluator()

    private val runtimeActionDispatcher =
        runtimeComposition.actionDispatcher()








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
        return runtimeComposition.runtimeStateHolder().state()
    }

    fun snapshot(): CoreDiagnosticSnapshot {
        return CoreDiagnosticSnapshot(
            runtimeState = runtimeComposition.runtimeStateHolder().state(),
            moduleStates = runtimeComposition.moduleManagerHolder().get()?.getModuleStates()
                ?: runtimeComposition.runtimeStateHolder().moduleStates(),
            runtimeServiceStates = runtimeComposition.runtimeServiceBootstrapHolder().get().getStates(),
            runtimeServiceFailures = runtimeComposition.runtimeServiceBootstrapHolder().get().getFailures(),
            runtimeServiceHealth = runtimeComposition.runtimeServiceBootstrapHolder().get().getHealth(),
            runtimeRecoverySnapshot = runtimeComposition.runtimeServiceBootstrapHolder().get().getRecoverySnapshot(),
            runtimeStatusSnapshot = getRuntimeStatusSnapshot(),
            failureReason = runtimeComposition.runtimeStateHolder().failureReason()
        )
    }

    fun diagnostics(): CoreRuntimeDiagnosticsSnapshot {
        return runtimeDiagnosticsService.snapshot()
    }

    fun monitor(): pro.liliya.core.runtime.monitor.RuntimeMonitorSnapshot {
        return runtimeMonitor.snapshot()
    }

    fun registerRuntimeService(service: RuntimeService) {
        runtimeComposition.runtimeServiceBootstrapHolder().get().register(service)
    }

    internal fun setRuntimeServiceProvider(
        provider: RuntimeServiceProvider
    ) {
        runtimeComposition.runtimeServiceProviderHolder().set(provider)
        runtimeComposition.runtimeServiceBootstrapHolder().set(
            runtimeComposition.createServiceBootstrap(
                runtimeComposition.runtimeServiceProviderHolder().get()
            )
        )

    }

    internal fun resetRuntimeServiceProvider() {
        runtimeComposition.runtimeServiceProviderHolder().reset()

        runtimeComposition.runtimeServiceBootstrapHolder().set(
            runtimeComposition.createServiceBootstrap(
                runtimeComposition.runtimeServiceProviderHolder().get()
            )
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
        runtimeComposition.observerRegistry().subscribe(observer)
    }

    fun unregisterRuntimeObserver(observer: RuntimeObserver) {
        runtimeComposition.observerRegistry().unsubscribe(observer)
    }

    fun getRuntimeTelemetrySnapshot():
        RuntimeTelemetrySnapshot {
        return runtimeTelemetryObserver.snapshot()
    }

    fun getRuntimeHealthSnapshot():
            RuntimeHealthSnapshot {
        return runtimeComposition.healthProvider().createSnapshot(
            state = runtimeComposition.runtimeStateHolder().state(),
            telemetry = runtimeTelemetryObserver.snapshot(),
            failureReason = runtimeComposition.runtimeStateHolder().failureReason()
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
        return runtimeComposition.healthReportProvider().createReport(
            state = runtimeComposition.runtimeStateHolder().state(),
            telemetry = runtimeTelemetryObserver.snapshot(),
            failure = runtimeFailureTracker.snapshot(),
            recovery = runtimeRecoveryTracker.snapshot()
        )
    }
    
    fun getRuntimeStatusSnapshot(): RuntimeStatusSnapshot {
        return runtimeComposition.statusProvider().createStatus(
            report = getRuntimeHealthReport()
        )
    }



    fun getRuntimeCommandHistory(): List<RuntimeCommandRecord> {
        return runtimeCommandHistory.snapshot()
    }

    fun getRuntimeActionAudit(): List<RuntimeActionAuditRecord> {
        return runtimeComposition.actionAuditProvider().snapshot()
    }


    fun getRuntimeState(): CoreRuntimeState {
        return runtimeComposition.runtimeStateHolder().state()
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
                previousState = runtimeComposition.runtimeStateHolder().state(),
                currentState = runtimeComposition.runtimeStateHolder().state(),
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
        if (runtimeComposition.runtimeBridgeStateHolder().isRuntimeObserverBridgeInstalled()) {
            return
        }

        runtimeObserverBridge.install()

        runtimeComposition.observerRegistry().subscribe(
            runtimeTelemetryObserver
        )
        runtimeComposition.runtimeBridgeStateHolder().markRuntimeObserverBridgeInstalled()
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

        runtimeComposition.runtimeBridgeStateHolder().markModuleEventBridgeInstalled()
    }

    fun start() {
        if (runtimeComposition.runtimeStateHolder().state() == CoreRuntimeState.RUNNING ||
            runtimeComposition.runtimeStateHolder().state() == CoreRuntimeState.STARTING
        ) {
            return
        }

        runtimeComposition.runtimeStateHolder().setState(CoreRuntimeState.STARTING)

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

        val manager = runtimeComposition.createModuleRuntime()

            try {
            runtimeComposition.moduleManagerHolder().set(manager)

            manager.loadModules()
            manager.startModules()

            runtimeComposition.runtimeServiceBootstrapHolder().get().start()

            runtimeComposition.registerRuntimeControls()
            runtimeComposition.registerRuntimeActionHandlers()
            runtimeComposition.runtimeStateHolder().setState(CoreRuntimeState.RUNNING)

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

            runtimeComposition.runtimeStateHolder().setModuleStates(manager.getModuleStates())

            try {
                manager.stopModules()
            } catch (_: Exception) {
            }

            runtimeComposition.moduleManagerHolder().clear()
            runtimeComposition.runtimeStateHolder().setFailureReason(error.message ?: "unknown")
            runtimeComposition.runtimeStateHolder().setState(CoreRuntimeState.FAILED)

            runtimeLifecycleRecorder.record(
                RuntimeLifecycleEvent.FAILED,
                runtimeComposition.runtimeStateHolder().failureReason()
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
        runtimeComposition.setModuleProvider(provider)
    }

    internal fun resetModuleProvider() {
        runtimeComposition.resetModuleProvider()
    }

    fun stop() {
        if (runtimeComposition.runtimeStateHolder().state() == CoreRuntimeState.STOPPED) {
            return
        }

        runtimeComposition.runtimeServiceBootstrapHolder().get().stop()

        runtimeComposition.moduleManagerHolder().get()?.stopModules()

        runtimeComposition.moduleManagerHolder().clear()
          runtimeComposition.runtimeStateHolder().setState(CoreRuntimeState.STOPPED)

        runtimeLifecycleRecorder.record(
            RuntimeLifecycleEvent.STOPPED
        )
          runtimeComposition.runtimeStateHolder().setFailureReason(null)
          runtimeComposition.runtimeStateHolder().setModuleStates(emptyMap())

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

        runtimeComposition.observerRegistry().unsubscribe(
            runtimeTelemetryObserver
        )

        runtimeComposition.runtimeBridgeStateHolder().reset()

        logger.info(
            LogConfig.SYSTEM_STOP,
            "Core runtime stopped"
        )
    }
}
