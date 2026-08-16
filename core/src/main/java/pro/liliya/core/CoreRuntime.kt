package pro.liliya.core

import pro.liliya.core.logging.LogConfig
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceProvider
import pro.liliya.core.runtime.composition.RuntimeCompositionFactory
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
        return runtimeComposition.commandHistoryProvider().snapshot()
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

        try {
            runtimeComposition.startRuntime()

            runtimeComposition.recordRuntimeStarted()
            runtimeComposition.publishRuntimeStartedDiagnostic()
            runtimeComposition.markRuntimeRecovered()
            runtimeComposition.publishRuntimeReady()

            runtimeComposition.logger().info(
                LogConfig.MODULE_READY,
                "Core runtime ready"
            )
        } catch (error: Exception) {
            runtimeComposition.logger().info(
                LogConfig.ERROR_CAUGHT,
                "Core runtime startup failed: ${error.message}"
            )

            runtimeComposition.moduleManager()?.let {
                runtimeComposition.setModuleStates(it.getModuleStates())
            }

            runtimeComposition.clearModuleRuntime()

            runtimeComposition.markRuntimeFailed(
                error.message ?: "unknown"
            )

            runtimeComposition.recordRuntimeFailure(
                runtimeComposition.failureReason()
            )

            runtimeComposition.publishRuntimeFailedDiagnostic()

            runtimeComposition.publishRuntimeFailed(
                error.message ?: "unknown"
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

        runtimeComposition.stopRuntime()

        runtimeComposition.logger().info(
            LogConfig.SYSTEM_STOP,
            "Core runtime stopped"
        )
    }

}
