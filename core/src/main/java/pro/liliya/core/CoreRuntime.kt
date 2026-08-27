package pro.liliya.core

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
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.intelligence.autonomous.RuntimeAutonomousIntelligenceCyclePipelineResult

object CoreRuntime {

    private val runtimeComposition =
        RuntimeCompositionFactory.create()














    fun state(): CoreRuntimeState {
        return runtimeComposition.runtimeState()
    }

    fun snapshot(): CoreDiagnosticSnapshot {
        return runtimeComposition.createDiagnosticSnapshot()
    }

    fun diagnostics(): CoreDiagnosticSnapshot {
        return snapshot()
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
        runtimeComposition.prepareRuntime()
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

    fun processAutonomousIntelligenceCycle(
        source: String,
        authority: RuntimeActionAuthorityContext
    ): RuntimeAutonomousIntelligenceCyclePipelineResult {
        if (state() != CoreRuntimeState.RUNNING) {
            throw IllegalStateException(
                "Autonomous intelligence cycle requires RUNNING runtime"
            )
        }

        return runtimeComposition
            .autonomousIntelligenceCyclePipeline()
            .process(
                source = source,
                authority = authority
            )
    }

    fun dispatchRuntimeAction(
        request: pro.liliya.core.runtime.action.RuntimeActionRequest
    ): pro.liliya.core.runtime.action.RuntimeActionResult {
        return runtimeComposition.actionDispatcher().dispatch(request)
    }

    fun executeRuntimeCommand(
        command: pro.liliya.core.runtime.control.RuntimeCommand
    ): pro.liliya.core.runtime.control.RuntimeControlResult {
        return runtimeComposition.defaultRuntimeControl().execute(
            pro.liliya.core.runtime.action.RuntimeActionRequest(
                command = command,
                source = "core-runtime-command"
            )
        )
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

        if (state() == CoreRuntimeState.RUNNING) {
            return
        }

        runtimeComposition.startLifecycle()

    }

    internal fun setModuleProvider(
        provider: pro.liliya.core.module.ModuleProvider
    ) {
        runtimeComposition.installModuleProvider(provider)
    }

    internal fun resetModuleProvider() {
        runtimeComposition.removeModuleProvider()
    }

    fun stop() {
        if (state() == CoreRuntimeState.STOPPED) {
            return
        }

        runtimeComposition.stopRuntime()
    }

}
