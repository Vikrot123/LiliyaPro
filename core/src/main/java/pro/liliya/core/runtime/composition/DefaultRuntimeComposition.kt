package pro.liliya.core.runtime.composition

import pro.liliya.core.logging.Logger
import pro.liliya.core.logging.LoggerFactory
import pro.liliya.core.logging.LogConfig

import pro.liliya.core.CoreDiagnosticEvent
import pro.liliya.core.CoreDiagnosticEventType

import pro.liliya.core.RuntimeEventBus
import pro.liliya.core.RuntimeEvent
import pro.liliya.core.ModuleEvent
import pro.liliya.core.ModuleEventBus

import pro.liliya.core.CoreDiagnosticSnapshot
import pro.liliya.core.CoreDiagnosticSource
import pro.liliya.core.CoreRuntimeStateHolder
import pro.liliya.core.CoreRuntimeState
import pro.liliya.core.CoreRuntimeDiagnostics
import pro.liliya.core.CoreRuntimeDiagnosticsService
import pro.liliya.core.DefaultCoreRuntimeDiagnosticsService
import pro.liliya.core.CoreDiagnosticProvider
import pro.liliya.core.CoreRuntimeContext
import pro.liliya.core.CoreDiagnosticEventBus
import pro.liliya.core.module.ModuleProvider
import pro.liliya.core.module.ModuleExceptionHandler
import pro.liliya.core.module.ModuleDependencyResolver
import pro.liliya.core.module.ModuleProviderHolder
import pro.liliya.core.module.ModuleRegistry
import pro.liliya.core.module.ModuleManager
import pro.liliya.core.module.ModuleManagerHolder
import pro.liliya.core.module.ModuleState
import pro.liliya.core.runtime.lifecycle.DefaultRuntimeLifecycleRecorder
import pro.liliya.core.runtime.lifecycle.RuntimeLifecycleRecorder
import pro.liliya.core.runtime.lifecycle.RuntimeLifecycleRecorderHolder
import pro.liliya.core.runtime.lifecycle.RuntimeLifecycleEvent
import pro.liliya.core.runtime.service.composition.DefaultRuntimeServiceComposition
import pro.liliya.core.runtime.module.composition.DefaultRuntimeModuleComposition

import pro.liliya.core.runtime.observer.DefaultRuntimeObserverRegistry
import pro.liliya.core.runtime.observer.RuntimeObserverBridge
import pro.liliya.core.runtime.module.ModuleEventBridge
import pro.liliya.core.runtime.policy.DefaultRuntimeActionPolicyEvaluator
import pro.liliya.core.runtime.capability.RuntimeCapabilityRegistry
import pro.liliya.core.runtime.capability.RuntimeCapabilityInfrastructure
import pro.liliya.core.runtime.capability.DefaultRuntimeCapabilityInfrastructureProvider
import pro.liliya.core.runtime.capability.DefaultRuntimeCapabilityRegistry
import pro.liliya.core.runtime.capability.RuntimeCapabilityAuthorityEvaluator
import pro.liliya.core.runtime.capability.DefaultRuntimeCapabilityAuthorityEvaluator
import pro.liliya.core.runtime.capability.RuntimeCapabilityResolver
import pro.liliya.core.runtime.policy.RuntimeActionPolicyEvaluator
import pro.liliya.core.runtime.health.RuntimeHealthProvider
import pro.liliya.core.runtime.health.RuntimeFailureTracker
import pro.liliya.core.runtime.health.RuntimeRecoveryTracker
import pro.liliya.core.runtime.health.RuntimeHealthReportProvider
import pro.liliya.core.runtime.health.RuntimeFailureHealthSnapshot
import pro.liliya.core.runtime.health.RuntimeHealthReport
import pro.liliya.core.runtime.status.RuntimeStatusSnapshot
import pro.liliya.core.runtime.status.RuntimeStatusProvider
import pro.liliya.core.runtime.monitor.DefaultRuntimeMonitor
import pro.liliya.core.runtime.monitor.RuntimeMonitor
import pro.liliya.core.runtime.telemetry.RuntimeTelemetryObserver
import pro.liliya.core.runtime.control.RuntimeControlRegistry
import pro.liliya.core.runtime.control.composition.RuntimeControlComposition
import pro.liliya.core.runtime.control.composition.DefaultRuntimeControlComposition
import pro.liliya.core.runtime.action.RuntimeActionExecutor

import pro.liliya.core.runtime.dispatcher.HealthRuntimeActionHandler

import pro.liliya.core.runtime.control.DefaultRuntimeControl

import pro.liliya.core.runtime.control.RuntimeControl

import pro.liliya.core.runtime.history.RuntimeCommandHistoryProvider
import pro.liliya.core.runtime.audit.RuntimeActionAuditProvider
import pro.liliya.core.runtime.dispatcher.RuntimeActionHandlerRegistry
import pro.liliya.core.runtime.dispatcher.RuntimeActionDispatcher
import pro.liliya.core.runtime.RuntimeServiceBootstrap
import pro.liliya.core.runtime.RuntimeServiceBootstrapHolder
import pro.liliya.core.runtime.RuntimeSupervisor
import pro.liliya.core.runtime.RuntimeRecoveryManager
import pro.liliya.core.runtime.RuntimeBridgeStateHolder
import pro.liliya.core.runtime.RuntimeModuleBridgeStateHolder
import pro.liliya.core.runtime.RuntimeServiceProvider
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.RuntimeServiceFailure
import pro.liliya.core.runtime.RuntimeServiceHealth
import pro.liliya.core.runtime.health.RuntimeRecoverySnapshot as HealthRecoverySnapshot
import pro.liliya.core.runtime.health.RuntimeHealthSnapshot
import pro.liliya.core.runtime.telemetry.RuntimeTelemetrySnapshot
import pro.liliya.core.runtime.RuntimeServiceProviderHolder
import pro.liliya.core.runtime.RuntimeRecoverySnapshot
import pro.liliya.core.runtime.RuntimeServiceRegistry

class DefaultRuntimeComposition :
        RuntimeComposition {

    private val diagnosticSource: CoreDiagnosticSource =
        CoreDiagnosticProvider()


    private val logger: Logger
        get() = LoggerFactory.create(
            module = "CORE",
            component = "CoreRuntime",
            method = "lifecycle"
        )






    private val diagnosticsService: CoreRuntimeDiagnosticsService =
        DefaultCoreRuntimeDiagnosticsService(
            diagnosticSource
        )


    private val context = CoreRuntimeContext(
        diagnosticEventBus = CoreDiagnosticEventBus(),
        diagnosticService = diagnosticsService
    )


    private val runtimeStateHolder =
        CoreRuntimeStateHolder()

    private val runtimeBridgeStateHolder =
        RuntimeBridgeStateHolder()

    private val runtimeModuleBridgeStateHolder =
        RuntimeModuleBridgeStateHolder()

    private val observerRegistry =
        DefaultRuntimeObserverRegistry()

    private val observerBridge =
        RuntimeObserverBridge(observerRegistry)

    private val moduleEventBridge =
        ModuleEventBridge(this)

    private val capabilityRegistry: RuntimeCapabilityRegistry =
        DefaultRuntimeCapabilityRegistry()

    private val capabilityInfrastructure: RuntimeCapabilityInfrastructure =
        DefaultRuntimeCapabilityInfrastructureProvider()
            .provide()

    private val capabilityAuthorityEvaluator: RuntimeCapabilityAuthorityEvaluator =
        DefaultRuntimeCapabilityAuthorityEvaluator()

    private val capabilityResolver =
        RuntimeCapabilityResolver(
            registry = capabilityRegistry,
            authorityEvaluator = capabilityAuthorityEvaluator
        )

    private val actionPolicyEvaluator =
        DefaultRuntimeActionPolicyEvaluator(
            capabilityResolver
        )

    private val lifecycleRecorder: RuntimeLifecycleRecorder =
        DefaultRuntimeLifecycleRecorder()

    private val lifecycleRecorderHolder =
        RuntimeLifecycleRecorderHolder(lifecycleRecorder)











    private val runtimeMonitor =
        DefaultRuntimeMonitor(
            this
        )

    private val runtimeHealthProvider =
        RuntimeHealthProvider()

    private val failureTracker =
        RuntimeFailureTracker()

    private val recoveryTracker =
        RuntimeRecoveryTracker()

    private val telemetryObserver = RuntimeTelemetryObserver()

    private val healthReportProvider = RuntimeHealthReportProvider(
        runtimeHealthProvider
    )

    private val statusProvider = RuntimeStatusProvider()

    private val controlComposition: RuntimeControlComposition =
        DefaultRuntimeControlComposition(
            this,
            actionPolicyEvaluator
        )

    private val defaultServiceComposition =
        DefaultRuntimeServiceComposition()

    private val serviceComposition: DefaultRuntimeServiceComposition =
        defaultServiceComposition


    private val moduleComposition: DefaultRuntimeModuleComposition =
        DefaultRuntimeModuleComposition(
            this
        )

    override fun diagnosticsService(): CoreRuntimeDiagnosticsService {
        return diagnosticsService
    }

    fun diagnosticEventBus(): CoreDiagnosticEventBus {
        return context.diagnosticEventBus
    }

    override fun context(): CoreRuntimeContext {
        return context
    }

    override fun createModuleRegistry(): ModuleRegistry {
        return moduleComposition.createModuleRegistry()
    }

    override fun createModuleManager(
        registry: ModuleRegistry,
        provider: ModuleProvider
    ): ModuleManager {
        return moduleComposition.createModuleManager(
            registry,
            provider
        )
    }

    override fun createModuleRuntime(): ModuleManager {
        return moduleComposition.createModuleRuntime()
    }

    override fun startRuntimeComponents(): ModuleManager {
        val manager = createModuleRuntime()

        setModuleManager(manager)

        startModuleRuntime(manager)

        serviceBootstrap().start()
        registerRuntimeControls()
        registerRuntimeActionHandlers()

        return manager
    }

    override fun prepareRuntimeStartup() {
        setRuntimeState(CoreRuntimeState.STARTING)
    }

    override fun startRuntimeLifecycle(): ModuleManager {

        val manager = startRuntimeComponents()

        setRuntimeState(CoreRuntimeState.RUNNING)

        return manager
    }

    override fun stopRuntimeLifecycle() {
        serviceBootstrap().stop()

        moduleManager()?.let {
            setModuleStates(it.getModuleStates())
            stopModuleRuntime(it)
        }

        clearModuleRuntime()
        setRuntimeState(CoreRuntimeState.STOPPED)
        setFailureReason(null)
        setModuleStates(emptyMap())
    }


    override fun startRuntime() {
        if (runtimeState() == CoreRuntimeState.RUNNING) {
            return
        }

        resetRuntimeHealth()
        installRuntimeObserverBridge()
        installModuleEventBridge()

        publishSystemStart()
        prepareRuntimeStartup()
        publishRuntimeStarting()

        try {
            startRuntimeLifecycle()
            recordRuntimeStarted()
            publishRuntimeStartedDiagnostic()
            markRuntimeRecovered()
            publishRuntimeReady()
            logRuntimeStartupSuccess()
        } catch (error: Exception) {
            moduleManager()?.let {
                try {
                    stopModuleRuntime(it)
                } catch (_: Exception) {
                }
            }

            moduleManager()?.let {
                setModuleStates(it.getModuleStates())
            }

            clearModuleRuntime()

            markRuntimeFailed(
                error.message ?: "unknown"
            )

            recordRuntimeFailure(
                failureReason()
            )

            publishRuntimeFailedDiagnostic()

            publishRuntimeFailed(
                error.message ?: "unknown"
            )

            logRuntimeStartupFailure(error)

            throw error
        }
    }

    fun start() {
        startLifecycle()
    }
    fun stop() {
        stopLifecycle()
    }


    override fun startLifecycle() {
        startRuntime()
    }

    override fun logRuntimeStartupSuccess() {
        logger().info(
            LogConfig.MODULE_READY,
            "Core runtime ready"
        )
    }

    override fun logRuntimeStartupFailure(
        error: Exception
    ) {
        logger().info(
            LogConfig.ERROR_CAUGHT,
            "Core runtime startup failed: ${error.message}"
        )
    }

    override fun logRuntimeStopped() {
        logger().info(
            LogConfig.SYSTEM_STOP,
            "Core runtime stopped"
        )
    }

    override fun handleRuntimeStartupFailure(
        error: Exception
    ) {
        moduleManager()?.let {
            setModuleStates(it.getModuleStates())
        }

        clearModuleRuntime()

        markRuntimeFailed(
            error.message ?: "unknown"
        )

        recordRuntimeFailure(
            failureReason()
        )

        publishRuntimeFailedDiagnostic()

        publishRuntimeFailed(
            error.message ?: "unknown"
        )
    }

    override fun stopLifecycle() {
        stopRuntime()
    }

    override fun stopRuntime() {
        if (runtimeState() == CoreRuntimeState.STOPPED &&
            moduleManager() == null
        ) {
            return
        }

        stopRuntimeLifecycle()

        recordRuntimeStopped()

        setFailureReason(null)
        setModuleStates(emptyMap())

        publishRuntimeStoppedDiagnostic()
        publishSystemStop()

        stopRuntimeBridges()
    }

    override fun startModuleRuntime(
        manager: ModuleManager
    ) {
        manager.loadModules()
        manager.startModules()
    }

    override fun stopModuleRuntime(
        manager: ModuleManager
    ) {
        moduleComposition.stopModuleRuntime(manager)
    }

    override fun clearModuleRuntime() {
        moduleComposition.clearModuleRuntime()
    }

    override fun moduleProviderHolder(): ModuleProviderHolder {
        return moduleComposition.moduleProviderHolder()
    }

    override fun moduleExceptionHandler(): ModuleExceptionHandler {
        return moduleComposition.moduleExceptionHandler()
    }

    override fun moduleDependencyResolver(): ModuleDependencyResolver {
        return moduleComposition.moduleDependencyResolver()
    }

    override fun setModuleProvider(
        provider: ModuleProvider
    ) {
        moduleComposition.setModuleProvider(provider)
    }

    override fun installModuleProvider(
        provider: ModuleProvider
    ) {
        setModuleProvider(provider)
    }

    override fun removeModuleProvider() {
        resetModuleProvider()
    }

    override fun resetModuleProvider() {
        moduleComposition.resetModuleProvider()
    }

    override fun moduleManagerHolder(): ModuleManagerHolder {
        return moduleComposition.moduleManagerHolder()
    }

    override fun moduleManager(): ModuleManager? {
        return moduleComposition.moduleManager()
    }

    override fun setModuleManager(manager: ModuleManager) {
        moduleComposition.setModuleManager(manager)
    }

    override fun clearModuleManager() {
        moduleComposition.clearModuleRuntime()
    }

    override fun runtimeStateHolder(): CoreRuntimeStateHolder {
        return runtimeStateHolder
    }

    override fun runtimeState(): CoreRuntimeState {
        return runtimeStateHolder.state()
    }

    override fun setRuntimeState(state: CoreRuntimeState) {
        runtimeStateHolder.setState(state)
    }

    override fun resetRuntimeState() {
        runtimeStateHolder.reset()
    }

    override fun failureReason(): String? {
        return runtimeStateHolder.failureReason()
    }

    override fun setFailureReason(reason: String?) {
        runtimeStateHolder.setFailureReason(reason)
    }

    override fun markRuntimeFailed(reason: String) {
        setFailureReason(reason)
        setRuntimeState(CoreRuntimeState.FAILED)
    }

    override fun moduleStates(): Map<String, ModuleState> {
        return runtimeStateHolder.moduleStates()
    }

    override fun setModuleStates(states: Map<String, ModuleState>) {
        runtimeStateHolder.setModuleStates(states)
    }

    override fun runtimeBridgeStateHolder(): RuntimeBridgeStateHolder {
        return runtimeBridgeStateHolder
    }

    override fun runtimeModuleBridgeStateHolder(): RuntimeModuleBridgeStateHolder {
        return runtimeModuleBridgeStateHolder
    }

    override fun isRuntimeObserverBridgeInstalled(): Boolean {
        return runtimeBridgeStateHolder.isRuntimeObserverBridgeInstalled()
    }

    override fun markRuntimeObserverBridgeInstalled() {
        runtimeBridgeStateHolder.markRuntimeObserverBridgeInstalled()
    }

    override fun resetRuntimeBridgeState() {
        runtimeBridgeStateHolder.reset()
    }

    override fun stopRuntimeBridges() {
        uninstallModuleEventBridge()
        uninstallRuntimeObserverBridge()

        observerRegistry.unsubscribe(
            telemetryObserver()
        )


        resetRuntimeBridgeState()
    }


    override fun moduleProvider(): ModuleProvider {
        return moduleComposition.moduleProvider()
    }

    override fun observerRegistry(): DefaultRuntimeObserverRegistry {
        return observerRegistry
    }


    override fun observerBridge(): RuntimeObserverBridge {
        return observerBridge
    }

    override fun installRuntimeObserverBridge() {
        if (!isRuntimeObserverBridgeInstalled()) {
            observerBridge.install()
            markRuntimeObserverBridgeInstalled()
        }

        observerRegistry.subscribe(
            telemetryObserver()
        )
    }


    fun uninstallRuntimeObserverBridge() {
        observerBridge.uninstall()
        resetRuntimeBridgeState()
    }

    override fun actionPolicyEvaluator(): RuntimeActionPolicyEvaluator {
        return actionPolicyEvaluator
    }

    override fun capabilityRegistry(): RuntimeCapabilityRegistry {
        return capabilityRegistry
    }

    override fun capabilityInfrastructure(): RuntimeCapabilityInfrastructure {
        return capabilityInfrastructure
    }

    override fun capabilityAuthorityEvaluator(): RuntimeCapabilityAuthorityEvaluator {
        return capabilityAuthorityEvaluator
    }

    override fun capabilityResolver(): RuntimeCapabilityResolver {
        return capabilityResolver
    }



    override fun lifecycleRecorder(): RuntimeLifecycleRecorder {
        return lifecycleRecorderHolder.get()
    }

    override fun lifecycleRecorderHolder(): RuntimeLifecycleRecorderHolder {
        return lifecycleRecorderHolder
    }

    override fun recordRuntimeStarted() {
        lifecycleRecorder().record(
            RuntimeLifecycleEvent.STARTED
        )
    }

    override fun recordRuntimeStopped() {
        lifecycleRecorder().record(
            RuntimeLifecycleEvent.STOPPED
        )
    }

    override fun recordRuntimeFailure(reason: String?) {
        lifecycleRecorder().record(
            RuntimeLifecycleEvent.FAILED,
            reason
        )
    }

    override fun recordRuntimeFailure(
        reason: String,
        module: String
    ) {
        failureTracker.recordFailure(
            reason = reason,
            module = module
        )

        lifecycleRecorder().record(
            RuntimeLifecycleEvent.FAILED,
            "$module: $reason"
        )
    }


    override fun publishSystemStart() {
        RuntimeEventBus.publish(
            RuntimeEvent.SystemStart
        )
    }

    override fun publishRuntimeStarting() {
        logger().info(
            LogConfig.MODULE_INIT,
            "Core runtime starting"
        )

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeStarting
        )
    }

    override fun publishRuntimeReady() {
        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeReady
        )
    }

    override fun publishRuntimeFailed(reason: String) {
        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeFailed(reason)
        )
    }

    override fun publishSystemStop() {
        RuntimeEventBus.publish(
            RuntimeEvent.SystemStop
        )
    }

    override fun publishModuleFailed(
        moduleName: String,
        reason: String
    ) {
        failureTracker.recordFailure(
            reason = reason,
            module = moduleName
        )

        lifecycleRecorder().record(
            RuntimeLifecycleEvent.FAILED,
            "$moduleName: $reason"
        )

        RuntimeEventBus.publish(
            RuntimeEvent.ModuleFailed(
                moduleName = moduleName,
                reason = reason
            )
        )
    }

    override fun installModuleEventBridge() {
        if (!runtimeModuleBridgeStateHolder.isModuleEventBridgeInstalled()) {
            moduleEventBridge.install()
            runtimeModuleBridgeStateHolder.markModuleEventBridgeInstalled()
        }
    }

    override fun uninstallModuleEventBridge() {
        moduleEventBridge.uninstall()
        runtimeModuleBridgeStateHolder.reset()
    }

    override fun publishRuntimeStartedDiagnostic() {
        diagnosticEventBus().publish(
            CoreDiagnosticEvent(
                type = CoreDiagnosticEventType.RUNTIME_STARTED,
                snapshot = createDiagnosticSnapshot()
            )
        )
    }

    override fun publishRuntimeFailedDiagnostic() {
        diagnosticEventBus().publish(
            CoreDiagnosticEvent(
                type = CoreDiagnosticEventType.RUNTIME_FAILED,
                snapshot = createDiagnosticSnapshot()
            )
        )
    }

    override fun publishRuntimeStoppedDiagnostic() {
        diagnosticEventBus().publish(
            CoreDiagnosticEvent(
                type = CoreDiagnosticEventType.RUNTIME_STOPPED,
                snapshot = createDiagnosticSnapshot()
            )
        )
    }

    override fun resetRuntimeHealth() {
        telemetryObserver.reset()
        failureTracker.clear()
    }

    override fun markRuntimeRecovered() {
        recoveryTracker.markRecovered()
    }

    override fun healthReportProvider(): RuntimeHealthReportProvider {
        return healthReportProvider
    }

    override fun createHealthReport(
        state: CoreRuntimeState,
        telemetry: RuntimeTelemetrySnapshot,
        failure: RuntimeFailureHealthSnapshot,
        recovery: HealthRecoverySnapshot
    ): RuntimeHealthReport {
        return healthReportProvider.createReport(
            state = state,
            telemetry = telemetry,
            failure = failure,
            recovery = recovery
        )
    }

    override fun statusProvider(): RuntimeStatusProvider {
        return statusProvider
    }

    override fun createRuntimeStatus(
        report: RuntimeHealthReport
    ): RuntimeStatusSnapshot {
        return statusProvider.createStatus(
            report = report
        )
    }

    override fun telemetryObserver(): RuntimeTelemetryObserver {
        return telemetryObserver
    }

    override fun registerRuntimeControls() {
        controlComposition.controlRegistry()
            .register(controlComposition.defaultRuntimeControl())
    }

    override fun commandHistoryProvider(): RuntimeCommandHistoryProvider {
        return controlComposition.commandHistoryProvider()
    }

    override fun actionAuditProvider(): RuntimeActionAuditProvider {
        return controlComposition.actionAuditProvider()
    }

    override fun registerRuntimeActionHandlers() {
        controlComposition.actionHandlerRegistry()
            .register(controlComposition.healthRuntimeActionHandler())
    }



    override fun actionDispatcher(): RuntimeActionDispatcher {
        return controlComposition.actionDispatcher()
    }

    override fun defaultRuntimeControl(): RuntimeControl {
        return controlComposition.defaultRuntimeControl()
    }



    override fun serviceProvider(): RuntimeServiceProvider {
        return serviceComposition.serviceProvider()
    }

    override fun runtimeServiceProviderHolder(): RuntimeServiceProviderHolder {
        return serviceComposition.runtimeServiceProviderHolder()
    }

    override fun setRuntimeServiceProvider(provider: RuntimeServiceProvider) {
        serviceComposition.setRuntimeServiceProvider(provider)
    }

    override fun runtimeServiceProvider(): RuntimeServiceProvider {
        return serviceComposition.runtimeServiceProvider()
    }

    override fun resetRuntimeServiceProvider() {
        serviceComposition.resetRuntimeServiceProvider()
    }

    override fun configureRuntimeServiceProvider(
        provider: RuntimeServiceProvider
    ) {
        serviceComposition.configureRuntimeServiceProvider(provider)
    }

    override fun resetRuntimeServiceConfiguration() {
        serviceComposition.resetRuntimeServiceConfiguration()
    }


    override fun prepareRuntime() {
        resetRuntimeServiceConfiguration()
        resetRuntimeState()
    }


    override fun runtimeServiceRegistry(): RuntimeServiceRegistry {
        return serviceComposition.runtimeServiceRegistry()
    }

    override fun serviceBootstrap(): RuntimeServiceBootstrap {
        return serviceComposition.serviceBootstrap()
    }

    override fun runtimeServiceBootstrapHolder(): RuntimeServiceBootstrapHolder {
        return serviceComposition.runtimeServiceBootstrapHolder()
    }

    override fun replaceRuntimeServiceBootstrap(bootstrap: RuntimeServiceBootstrap) {
        serviceComposition.replaceRuntimeServiceBootstrap(bootstrap)
    }

    fun runtimeSupervisor(): RuntimeSupervisor {
        return serviceComposition.runtimeSupervisor()
    }

    fun createServiceBootstrap(
        provider: RuntimeServiceProvider
    ): RuntimeServiceBootstrap {
        return serviceComposition.createServiceBootstrap(provider)
    }

    fun runtimeRecoveryManager(): RuntimeRecoveryManager {
        return serviceComposition.runtimeRecoveryManager()
    }


    override fun registerRuntimeService(
        service: RuntimeService
    ) {
        serviceBootstrap().register(service)
    }

    override fun runtimeServiceStates(): Map<String, RuntimeServiceState> {
        return serviceComposition.runtimeServiceStates()
    }

    override fun runtimeServiceFailures(): List<RuntimeServiceFailure> {
        return serviceComposition.runtimeServiceFailures()
    }

    override fun runtimeServiceHealth(): Map<String, RuntimeServiceHealth> {
        return serviceComposition.runtimeServiceHealth()
    }

    override fun runtimeServiceRecoverySnapshot(): RuntimeRecoverySnapshot? {
        return serviceComposition.runtimeServiceRecoverySnapshot()
    }

    
    override fun runtimeMonitor(): RuntimeMonitor {
        return runtimeMonitor
    }




    override fun recoveryTracker(): RuntimeRecoveryTracker {
        return recoveryTracker
    }

    override fun failureTracker(): RuntimeFailureTracker {
        return failureTracker
    }

    override fun healthProvider(): RuntimeHealthProvider {
        return runtimeHealthProvider
    }

    override fun createHealthSnapshot(
        state: CoreRuntimeState,
        telemetry: RuntimeTelemetrySnapshot,
        failureReason: String?
    ): RuntimeHealthSnapshot {
        return runtimeHealthProvider.createSnapshot(
            state = state,
            telemetry = telemetry,
            failureReason = failureReason
        )
    }

    override fun runtimeHealthSnapshot(): RuntimeHealthSnapshot {
        return runtimeHealthProvider.createSnapshot(
            state = runtimeState(),
            telemetry = telemetryObserver().snapshot(),
            failureReason = failureReason()
        )
    }

    override fun runtimeHealthReport(): RuntimeHealthReport {
        return healthReportProvider.createReport(
            state = runtimeState(),
            telemetry = telemetryObserver().snapshot(),
            failure = failureTracker().snapshot(),
            recovery = recoveryTracker().snapshot()
        )
    }

    override fun runtimeStatusSnapshot(): RuntimeStatusSnapshot {
        return createRuntimeStatus(
            report = runtimeHealthReport()
        )
    }

    override fun createDiagnosticSnapshot(): CoreDiagnosticSnapshot {
        return CoreDiagnosticSnapshot(
            runtimeState = runtimeState(),
            moduleStates = moduleManager()?.getModuleStates()
                ?: moduleStates(),
            runtimeServiceStates = runtimeServiceStates(),
            runtimeServiceFailures = runtimeServiceFailures(),
            runtimeServiceHealth = runtimeServiceHealth(),
            runtimeRecoverySnapshot = runtimeServiceRecoverySnapshot(),
            runtimeStatusSnapshot = createRuntimeStatus(
                report = healthReportProvider.createReport(
                    state = runtimeState(),
                    telemetry = telemetryObserver().snapshot(),
                    failure = failureTracker().snapshot(),
                    recovery = recoveryTracker().snapshot()
                )
            ),
            failureReason = failureReason()
        )
    }

    override fun logger(): Logger {
        return logger
    }

    override fun diagnosticSource(): CoreDiagnosticSource {
        return diagnosticSource
    }


}
