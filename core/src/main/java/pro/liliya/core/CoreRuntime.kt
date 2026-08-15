package pro.liliya.core

import pro.liliya.core.logging.LogConfig
import pro.liliya.core.logging.Logger
import pro.liliya.core.logging.LoggerFactory
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceRegistry
import pro.liliya.core.runtime.RuntimeServiceProvider
import pro.liliya.core.runtime.RuntimeServiceBootstrap
import pro.liliya.core.runtime.composition.RuntimeCompositionFactory
import pro.liliya.core.runtime.lifecycle.RuntimeLifecycleRecorder
import pro.liliya.core.runtime.lifecycle.RuntimeLifecycleEvent
import pro.liliya.core.runtime.observer.RuntimeObserver
import pro.liliya.core.runtime.health.RuntimeHealthSnapshot
import pro.liliya.core.runtime.health.RuntimeRecoverySnapshot
import pro.liliya.core.runtime.health.RuntimeHealthReport
import pro.liliya.core.runtime.status.RuntimeStatusSnapshot
import pro.liliya.core.runtime.control.RuntimeControlResult
import pro.liliya.core.runtime.control.RuntimeControl
import pro.liliya.core.runtime.history.RuntimeCommandRecord
import pro.liliya.core.runtime.action.RuntimeActionResult
import pro.liliya.core.runtime.audit.RuntimeActionAuditRecord
import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.health.RuntimeFailureHealthSnapshot
import pro.liliya.core.runtime.telemetry.RuntimeTelemetrySnapshot

object CoreRuntime {

    private val runtimeComposition =
        RuntimeCompositionFactory.create()













    private val logger: Logger
        get() = LoggerFactory.create(
            module = "CORE",
            component = "CoreRuntime",
            method = "lifecycle"
        )

    fun state(): CoreRuntimeState {
        return runtimeComposition.runtimeState()
    }

    fun snapshot(): CoreDiagnosticSnapshot {
        return runtimeComposition.createDiagnosticSnapshot()
    }

    fun diagnostics(): CoreRuntimeDiagnosticsSnapshot {
        return runtimeComposition.diagnosticsService().snapshot()
    }

    fun monitor(): pro.liliya.core.runtime.monitor.RuntimeMonitorSnapshot {
        return runtimeComposition.runtimeMonitor().snapshot()
    }

    fun registerRuntimeService(service: RuntimeService) {
        runtimeComposition.registerRuntimeService(service)
    }

    internal fun setRuntimeServiceProvider(
        provider: RuntimeServiceProvider
    ) {
        runtimeComposition.configureRuntimeServiceProvider(provider)
    }

    internal fun resetRuntimeServiceProvider() {
        runtimeComposition.resetRuntimeServiceConfiguration()
    }

    private fun installRuntimeObserverBridge() {
        if (runtimeComposition.isRuntimeObserverBridgeInstalled()) {
            return
        }

        runtimeComposition.observerBridge().install()

        runtimeComposition.observerRegistry().subscribe(
            runtimeComposition.telemetryObserver()
        )

        runtimeComposition.markRuntimeObserverBridgeInstalled()
    }

    private fun installModuleEventBridge() {
        if (ModuleEventBus.hasListeners()) {
            return
        }

        ModuleEventBus.subscribe { event ->
            if (event is ModuleEvent.Failed) {
                runtimeComposition.recordRuntimeFailure(
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

        runtimeComposition.markModuleEventBridgeInstalled()
    }

    fun getRuntimeTelemetrySnapshot(): RuntimeTelemetrySnapshot {
        return runtimeComposition.telemetryObserver().snapshot()
    }

    fun getRuntimeHealthSnapshot(): RuntimeHealthSnapshot {
        return runtimeComposition.runtimeHealthSnapshot()
    }

    fun getRuntimeFailureHealthSnapshot(): RuntimeFailureHealthSnapshot {
        return runtimeComposition.failureTracker().snapshot()
    }

    fun getRuntimeRecoverySnapshot(): RuntimeRecoverySnapshot {
        return runtimeComposition.recoveryTracker().snapshot()
    }

    fun getRuntimeHealthReport(): RuntimeHealthReport {
        return runtimeComposition.runtimeHealthReport()
    }

    fun getRuntimeStatusSnapshot(): RuntimeStatusSnapshot {
        return runtimeComposition.runtimeStatusSnapshot()
    }

    fun getRuntimeState(): CoreRuntimeState {
        return runtimeComposition.runtimeState()
    }

    fun dispatchRuntimeAction(
        request: pro.liliya.core.runtime.action.RuntimeActionRequest
    ): pro.liliya.core.runtime.action.RuntimeActionResult {
        return runtimeComposition.actionDispatcher().dispatch(request)
    }

    fun executeRuntimeCommand(
        command: pro.liliya.core.runtime.control.RuntimeCommand
    ): pro.liliya.core.runtime.control.RuntimeControlResult {
        return runtimeComposition.defaultRuntimeControl().execute(command)
    }

    fun getRuntimeActionAudit():
            List<pro.liliya.core.runtime.audit.RuntimeActionAuditRecord> {
        return runtimeComposition.actionAuditProvider().snapshot()
    }

    fun getRuntimeCommandHistory():
            List<pro.liliya.core.runtime.history.RuntimeCommandRecord> {
        return runtimeComposition.commandHistory().snapshot()
    }

    fun runtimeControl(): pro.liliya.core.runtime.control.RuntimeControl {
        return runtimeComposition.defaultRuntimeControl()
    }

    fun registerRuntimeObserver(
        observer: pro.liliya.core.runtime.observer.RuntimeObserver
    ) {
        runtimeComposition.observerRegistry().subscribe(observer)
    }

    fun unregisterRuntimeObserver(
        observer: pro.liliya.core.runtime.observer.RuntimeObserver
    ) {
        runtimeComposition.observerRegistry().unsubscribe(observer)
    }

    fun start() {
        if (runtimeComposition.runtimeState() == CoreRuntimeState.RUNNING ||
            runtimeComposition.runtimeState() == CoreRuntimeState.STARTING
        ) {
            return
        }

        runtimeComposition.setRuntimeState(CoreRuntimeState.STARTING)

        runtimeComposition.resetRuntimeHealth()

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

        var manager: pro.liliya.core.module.ModuleManager? = null

        try {
            manager = runtimeComposition.startRuntimeComponents()

            runtimeComposition.setRuntimeState(CoreRuntimeState.RUNNING)

            runtimeComposition.lifecycleRecorder().record(
                RuntimeLifecycleEvent.STARTED
            )

            runtimeComposition.context().diagnosticEventBus.publish(
                CoreDiagnosticEvent(
                    type = CoreDiagnosticEventType.RUNTIME_STARTED,
                    snapshot = snapshot()
                )
            )

            runtimeComposition.markRuntimeRecovered()

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

            runtimeComposition.moduleManager()?.let {
                runtimeComposition.setModuleStates(it.getModuleStates())
            }

            manager?.let {
                try {
                    it.stopModules()
                } catch (_: Exception) {
                }
            }

            runtimeComposition.clearModuleRuntime()
            runtimeComposition.setFailureReason(error.message ?: "unknown")
            runtimeComposition.setRuntimeState(CoreRuntimeState.FAILED)

            runtimeComposition.lifecycleRecorder().record(
                RuntimeLifecycleEvent.FAILED,
                runtimeComposition.failureReason()
            )

            runtimeComposition.context().diagnosticEventBus.publish(
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
        if (runtimeComposition.runtimeState() == CoreRuntimeState.STOPPED) {
            return
        }

        runtimeComposition.stopRuntimeServices()

        runtimeComposition.moduleManager()?.let {
            runtimeComposition.stopModuleRuntime(it)
        }

        runtimeComposition.clearModuleRuntime()
          runtimeComposition.setRuntimeState(CoreRuntimeState.STOPPED)

        runtimeComposition.lifecycleRecorder().record(
            RuntimeLifecycleEvent.STOPPED
        )
          runtimeComposition.setFailureReason(null)
          runtimeComposition.setModuleStates(emptyMap())

        runtimeComposition.context().diagnosticEventBus.publish(
            CoreDiagnosticEvent(
                type = CoreDiagnosticEventType.RUNTIME_STOPPED,
                snapshot = snapshot()
            )
        )

        RuntimeEventBus.publish(
            RuntimeEvent.SystemStop
        )

        runtimeComposition.observerBridge().uninstall()

        runtimeComposition.observerRegistry().unsubscribe(
            runtimeComposition.telemetryObserver()
        )

        runtimeComposition.resetRuntimeBridgeState()

        logger.info(
            LogConfig.SYSTEM_STOP,
            "Core runtime stopped"
        )
    }
}
