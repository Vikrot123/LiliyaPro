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
import pro.liliya.core.runtime.orchestration.RuntimeLifecycleController
import pro.liliya.core.runtime.orchestration.DefaultRuntimeLifecycleController
import pro.liliya.core.runtime.orchestration.RuntimeLifecycleComposition
import pro.liliya.core.runtime.orchestration.RuntimeStartupComposition
import pro.liliya.core.runtime.orchestration.RuntimeShutdownComposition
import pro.liliya.core.runtime.orchestration.RuntimeServiceComposition
import pro.liliya.core.runtime.service.composition.DefaultRuntimeServiceComposition
import pro.liliya.core.runtime.orchestration.RuntimeModuleComposition
import pro.liliya.core.runtime.module.composition.DefaultRuntimeModuleComposition
import pro.liliya.core.runtime.orchestration.RuntimeBridgeComposition
import pro.liliya.core.runtime.orchestration.RuntimeActionComposition
import pro.liliya.core.runtime.orchestration.RuntimeEventComposition
import pro.liliya.core.runtime.orchestration.RuntimeHealthComposition
import pro.liliya.core.runtime.orchestration.RuntimeHealthController
import pro.liliya.core.runtime.orchestration.DefaultRuntimeHealthController
import pro.liliya.core.runtime.orchestration.RuntimeTelemetryController
import pro.liliya.core.runtime.orchestration.DefaultRuntimeTelemetryController
import pro.liliya.core.runtime.orchestration.RuntimeStatusComposition
import pro.liliya.core.runtime.orchestration.RuntimeStatusController
import pro.liliya.core.runtime.orchestration.DefaultRuntimeStatusController
import pro.liliya.core.runtime.orchestration.RuntimeActionController
import pro.liliya.core.runtime.orchestration.RuntimeEventController
import pro.liliya.core.runtime.orchestration.DefaultRuntimeEventController
import pro.liliya.core.runtime.orchestration.DefaultRuntimeActionController
import pro.liliya.core.runtime.orchestration.RuntimeBridgeController
import pro.liliya.core.runtime.orchestration.DefaultRuntimeBridgeController
import pro.liliya.core.runtime.orchestration.RuntimeModuleController
import pro.liliya.core.runtime.orchestration.DefaultRuntimeModuleController
import pro.liliya.core.runtime.orchestration.RuntimeServiceController
import pro.liliya.core.runtime.orchestration.DefaultRuntimeServiceController
import pro.liliya.core.runtime.orchestration.RuntimeShutdownController
import pro.liliya.core.runtime.orchestration.DefaultRuntimeShutdownController
import pro.liliya.core.runtime.orchestration.RuntimeStartupController
import pro.liliya.core.runtime.orchestration.DefaultRuntimeStartupController

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
import pro.liliya.core.runtime.telemetry.composition.DefaultRuntimeTelemetryProviderComposition
import pro.liliya.core.runtime.orchestration.RuntimeTelemetryComposition
import pro.liliya.core.runtime.telemetry.composition.RuntimeTelemetryProviderComposition
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
    RuntimeComposition,
    RuntimeLifecycleComposition,
    RuntimeStartupComposition,
        RuntimeShutdownComposition,
        RuntimeServiceComposition,
        RuntimeModuleComposition,
        RuntimeBridgeComposition,
        RuntimeActionComposition,
    RuntimeEventComposition,
    RuntimeTelemetryComposition,
        RuntimeHealthComposition,
    RuntimeTelemetryProviderComposition,
    RuntimeStatusComposition {

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

    private val lifecycleController: RuntimeLifecycleController =
        DefaultRuntimeLifecycleController(this)

    private val startupController: RuntimeStartupController =
        DefaultRuntimeStartupController(this)

    private val shutdownController: RuntimeShutdownController =
        DefaultRuntimeShutdownController(this)

    private val serviceController: RuntimeServiceController =
        DefaultRuntimeServiceController(this)

    private val moduleController: RuntimeModuleController =
        DefaultRuntimeModuleController(this)

    private val bridgeController: RuntimeBridgeController =
        DefaultRuntimeBridgeController(this)

    private val actionController: RuntimeActionController =
        DefaultRuntimeActionController(this)

    private val eventController: RuntimeEventController =
        DefaultRuntimeEventController(this)

    private val healthController: RuntimeHealthController =
        DefaultRuntimeHealthController(this)

    private val telemetryController: RuntimeTelemetryController =
        DefaultRuntimeTelemetryController(this)

    private val statusController: RuntimeStatusController =
        DefaultRuntimeStatusController(this)

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

    private val telemetryComposition: RuntimeTelemetryProviderComposition =
        DefaultRuntimeTelemetryProviderComposition(
            observer = telemetryObserver,
            health = runtimeHealthProvider,
            report = RuntimeHealthReportProvider(
                runtimeHealthProvider
            ),
            status = RuntimeStatusProvider()
        )

    private val controlComposition: RuntimeControlComposition =
        DefaultRuntimeControlComposition(
            this,
            actionPolicyEvaluator
        )

    private val serviceComposition: RuntimeServiceComposition =
        DefaultRuntimeServiceComposition(
            this
        )

    private val moduleComposition: RuntimeModuleComposition =
        DefaultRuntimeModuleComposition(
            this
        )

    override fun diagnosticsService(): CoreRuntimeDiagnosticsService {
        return diagnosticsService
    }

    override fun diagnosticEventBus(): CoreDiagnosticEventBus {
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
        val manager = moduleController.startRuntimeComponents()

        serviceController.startRuntimeServices()
        actionController.startRuntimeActions()

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
        serviceController.stopRuntimeServices()

        moduleController.stopRuntimeModules()
        stopRuntimeServices()
        clearModuleRuntime()
        setRuntimeState(CoreRuntimeState.STOPPED)
        setFailureReason(null)
        setModuleStates(emptyMap())
    }


    override fun startRuntime() {
        startupController.start()
    }

    override fun start() {
        startLifecycle()
    }
    override fun stop() {
        stopLifecycle()
    }


    override fun startLifecycle() {
        lifecycleController.start()
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
        lifecycleController.stop()
    }

    override fun stopRuntime() {
        shutdownController.stop()
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
        bridgeController.uninstall()

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

    override fun runtimeBridgeController(): RuntimeBridgeController {
        return bridgeController
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


    override fun uninstallRuntimeObserverBridge() {
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

    override fun lifecycleController(): RuntimeLifecycleController {
        return lifecycleController
    }

    override fun startupController(): RuntimeStartupController {
        return startupController
    }

    override fun shutdownController(): RuntimeShutdownController {
        return shutdownController
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
        eventController.publishSystemStart()
    }

    override fun publishRuntimeStarting() {
        logger().info(
            LogConfig.MODULE_INIT,
            "Core runtime starting"
        )

        eventController.publishRuntimeStarting()
    }

    override fun publishRuntimeReady() {
        eventController.publishRuntimeReady()
    }

    override fun publishRuntimeFailed(reason: String) {
        eventController.publishRuntimeFailed(reason)
    }

    override fun publishSystemStop() {
        eventController.publishSystemStop()
    }

    override fun publishModuleFailed(
        moduleName: String,
        reason: String
    ) {
        eventController.publishModuleFailed(
            moduleName,
            reason
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
        eventController.publishRuntimeStartedDiagnostic()
    }

    override fun publishRuntimeFailedDiagnostic() {
        eventController.publishRuntimeFailedDiagnostic()
    }

    override fun publishRuntimeStoppedDiagnostic() {
        eventController.publishRuntimeStoppedDiagnostic()
    }

    override fun resetRuntimeHealth() {
        telemetryComposition.telemetryObserver().reset()
        failureTracker.clear()
    }

    override fun markRuntimeRecovered() {
        recoveryTracker.markRecovered()
    }

    override fun healthReportProvider(): RuntimeHealthReportProvider {
        return telemetryComposition.healthReportProvider()
    }

    override fun createHealthReport(
        state: CoreRuntimeState,
        telemetry: RuntimeTelemetrySnapshot,
        failure: RuntimeFailureHealthSnapshot,
        recovery: HealthRecoverySnapshot
    ): RuntimeHealthReport {
        return telemetryComposition.healthReportProvider().createReport(
            state = state,
            telemetry = telemetry,
            failure = failure,
            recovery = recovery
        )
    }

    override fun statusProvider(): RuntimeStatusProvider {
        return telemetryComposition.statusProvider()
    }

    override fun createRuntimeStatus(
        report: RuntimeHealthReport
    ): RuntimeStatusSnapshot {
        return telemetryComposition.statusProvider().createStatus(
            report = report
        )
    }

    override fun telemetryObserver(): RuntimeTelemetryObserver {
        return telemetryComposition.telemetryObserver()
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

    override fun setRuntimeServiceBootstrap(bootstrap: RuntimeServiceBootstrap) {
        serviceComposition.setRuntimeServiceBootstrap(bootstrap)
    }

    override fun runtimeServiceBootstrap(): RuntimeServiceBootstrap {
        return serviceComposition.runtimeServiceBootstrap()
    }


    override fun registerRuntimeService(
        service: RuntimeService
    ) {
        serviceComposition.runtimeServiceBootstrap().register(service)
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

    override fun startRuntimeServices() {
        serviceComposition.runtimeServiceBootstrap().start()
    }

    override fun stopRuntimeServices() {
        serviceComposition.runtimeServiceBootstrap().stop()
    }

    override fun createServiceBootstrap(
        provider: RuntimeServiceProvider
    ): RuntimeServiceBootstrap {
        return serviceComposition.createServiceBootstrap(provider)
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
        return telemetryComposition.healthProvider()
    }

    override fun createHealthSnapshot(
        state: CoreRuntimeState,
        telemetry: RuntimeTelemetrySnapshot,
        failureReason: String?
    ): RuntimeHealthSnapshot {
        return telemetryComposition.healthProvider().createSnapshot(
            state = state,
            telemetry = telemetry,
            failureReason = failureReason
        )
    }

    override fun runtimeHealthSnapshot(): RuntimeHealthSnapshot {
        return healthController.createSnapshot(
            state = runtimeState(),
            telemetry = telemetryObserver().snapshot(),
            failureReason = failureReason()
        )
    }

    override fun runtimeHealthReport(): RuntimeHealthReport {
        return healthController.createReport(
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
                report = healthController.createReport(
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
