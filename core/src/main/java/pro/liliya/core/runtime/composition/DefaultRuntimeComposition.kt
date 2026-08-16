package pro.liliya.core.runtime.composition

import pro.liliya.core.logging.Logger
import pro.liliya.core.logging.LoggerFactory

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
import pro.liliya.core.module.ModuleProvider
import pro.liliya.core.module.ModuleExceptionHandler
import pro.liliya.core.module.ModuleDependencyResolver
import pro.liliya.core.module.ModuleProviderHolder
import pro.liliya.core.module.CoreModuleProvider
import pro.liliya.core.module.ModuleRegistry
import pro.liliya.core.module.ModuleManager
import pro.liliya.core.module.ModuleManagerHolder
import pro.liliya.core.module.ModuleState
import pro.liliya.core.runtime.lifecycle.DefaultRuntimeLifecycleRecorder
import pro.liliya.core.runtime.lifecycle.RuntimeLifecycleRecorder
import pro.liliya.core.runtime.lifecycle.RuntimeLifecycleRecorderHolder
import pro.liliya.core.runtime.lifecycle.RuntimeLifecycleEvent
import pro.liliya.core.runtime.observer.DefaultRuntimeObserverRegistry
import pro.liliya.core.runtime.observer.RuntimeObserverBridge
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
import pro.liliya.core.runtime.RuntimeServiceProvider
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.RuntimeServiceFailure
import pro.liliya.core.runtime.RuntimeServiceHealth
import pro.liliya.core.runtime.RuntimeRecoverySnapshot
import pro.liliya.core.runtime.health.RuntimeRecoverySnapshot as HealthRecoverySnapshot
import pro.liliya.core.runtime.health.RuntimeHealthSnapshot
import pro.liliya.core.runtime.telemetry.RuntimeTelemetrySnapshot
import pro.liliya.core.runtime.RuntimeServiceProviderHolder
import pro.liliya.core.runtime.CoreRuntimeServiceProvider
import pro.liliya.core.runtime.RuntimeServiceRegistry

class DefaultRuntimeComposition : RuntimeComposition {

    private val diagnosticSource: CoreDiagnosticSource =
        CoreDiagnosticProvider()


    private val logger: Logger =
        LoggerFactory.create(
            module = "CORE",
            component = "CoreRuntime",
            method = "lifecycle"
        )


    private var moduleEventBridgeListener: ((ModuleEvent) -> Unit)? = null
    private var runtimeFailureEventListener: ((RuntimeEvent) -> Unit)? = null




    private val runtimeDiagnostics: CoreRuntimeDiagnostics =
        CoreRuntimeDiagnostics(diagnosticSource)

    private val diagnosticsService: CoreRuntimeDiagnosticsService =
        DefaultCoreRuntimeDiagnosticsService(
            runtimeDiagnostics
        )


    private val context = CoreRuntimeContext()


    private val moduleProvider: ModuleProvider =
        CoreModuleProvider()


    private val moduleProviderHolder =
        ModuleProviderHolder(
            moduleProvider
        )

    private val moduleManagerHolder =
        ModuleManagerHolder()

    private val runtimeStateHolder =
        CoreRuntimeStateHolder()

    private val runtimeBridgeStateHolder =
        RuntimeBridgeStateHolder()

    private val observerRegistry =
        DefaultRuntimeObserverRegistry()

    private val observerBridge =
        RuntimeObserverBridge(observerRegistry)

    private val capabilityRegistry: RuntimeCapabilityRegistry =
        DefaultRuntimeCapabilityRegistry()

    private val capabilityInfrastructure: RuntimeCapabilityInfrastructure =
        DefaultRuntimeCapabilityInfrastructureProvider()
            .provide()

    private val moduleExceptionHandler =
        ModuleExceptionHandler()

    private val moduleDependencyResolver =
        ModuleDependencyResolver()


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

    private val healthProvider =
        RuntimeHealthProvider()

    private val failureTracker =
        RuntimeFailureTracker()

    private val recoveryTracker =
        RuntimeRecoveryTracker()

    private val healthReportProvider =
        RuntimeHealthReportProvider(
            healthProvider
        )

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
            actionPolicyEvaluator,
            this
        )

    private val defaultRuntimeControl =
        DefaultRuntimeControl(
            this
        )

    private val healthRuntimeActionHandler =
        HealthRuntimeActionHandler(
            RuntimeActionExecutor(
                defaultRuntimeControl
            )
        )

    private val serviceProvider =
        CoreRuntimeServiceProvider()

    private val runtimeServiceRegistry =
        RuntimeServiceRegistry()


    private val runtimeServiceProviderHolder =
        RuntimeServiceProviderHolder(
            serviceProvider
        )

    private val serviceBootstrap =
        createServiceBootstrap(
            serviceProvider
        )

    private val runtimeServiceBootstrapHolder =
        RuntimeServiceBootstrapHolder(
            serviceBootstrap
        )

    override fun diagnosticsService(): CoreRuntimeDiagnosticsService {
        return diagnosticsService
    }

    override fun context(): CoreRuntimeContext {
        return context
    }

    override fun createModuleRegistry(): ModuleRegistry {
        return ModuleRegistry(
            capabilityInfrastructure,
            moduleExceptionHandler,
            moduleDependencyResolver
        )
    }

    override fun createModuleManager(
        registry: ModuleRegistry,
        provider: ModuleProvider
    ): ModuleManager {
        return ModuleManager(
            registry = registry,
            provider = provider
        )
    }

    override fun createModuleRuntime(): ModuleManager {
        return ModuleManager(
            registry = createModuleRegistry(),
            provider = moduleProviderHolder.get()
        )
    }

    override fun startRuntimeComponents(): ModuleManager {
        val manager = createModuleRuntime()

        setModuleManager(manager)

        startModuleRuntime(manager)
        startRuntimeServices()
        registerRuntimeControls()
        registerRuntimeActionHandlers()

        return manager
    }

    override fun startRuntimeLifecycle(): ModuleManager {
        setRuntimeState(CoreRuntimeState.STARTING)

        resetRuntimeHealth()

        return startRuntimeComponents().also {
            setRuntimeState(CoreRuntimeState.RUNNING)
        }
    }

    override fun stopRuntimeLifecycle() {
        stopRuntimeServices()

        moduleManager()?.let {
            stopModuleRuntime(it)
        }

        clearModuleRuntime()

        setRuntimeState(CoreRuntimeState.STOPPED)

        setFailureReason(null)

        setModuleStates(emptyMap())
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
        manager.stopModules()
    }

    override fun clearModuleRuntime() {
        moduleManagerHolder.clear()
    }

    override fun moduleProviderHolder(): ModuleProviderHolder {
        return moduleProviderHolder
    }

    override fun moduleExceptionHandler(): ModuleExceptionHandler {
        return moduleExceptionHandler
    }

    override fun moduleDependencyResolver(): ModuleDependencyResolver {
        return moduleDependencyResolver
    }

    override fun setModuleProvider(
        provider: ModuleProvider
    ) {
        moduleProviderHolder.set(provider)
    }

    override fun resetModuleProvider() {
        moduleProviderHolder.reset()
    }

    override fun moduleManagerHolder(): ModuleManagerHolder {
        return moduleManagerHolder
    }

    override fun moduleManager(): ModuleManager? {
        return moduleManagerHolder.get()
    }

    override fun setModuleManager(manager: ModuleManager) {
        moduleManagerHolder.set(manager)
    }

    override fun clearModuleManager() {
        moduleManagerHolder.clear()
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

    override fun isRuntimeObserverBridgeInstalled(): Boolean {
        return runtimeBridgeStateHolder.isRuntimeObserverBridgeInstalled()
    }

    override fun markRuntimeObserverBridgeInstalled() {
        runtimeBridgeStateHolder.markRuntimeObserverBridgeInstalled()
    }

    override fun markModuleEventBridgeInstalled() {
        runtimeBridgeStateHolder.markModuleEventBridgeInstalled()
    }

    override fun resetRuntimeBridgeState() {
        runtimeBridgeStateHolder.reset()
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

    override fun publishSystemStart() {
        RuntimeEventBus.publish(
            RuntimeEvent.SystemStart
        )
    }

    override fun publishRuntimeStarting() {
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

        RuntimeEventBus.publish(
            RuntimeEvent.ModuleFailed(
                moduleName = moduleName,
                reason = reason
            )
        )
    }

    override fun installModuleEventBridge() {
        if (runtimeBridgeStateHolder.isModuleEventBridgeInstalled()
            && moduleEventBridgeListener != null
            && runtimeFailureEventListener != null
        ) {
            return
        }

        val moduleListener: (ModuleEvent) -> Unit = { event ->
            println("MODULE BRIDGE RECEIVED: $event")

            if (event is ModuleEvent.Failed) {
                val failureReason = "${event.moduleName}: ${event.phase}: ${event.reason}"

                println("MODULE BRIDGE PUBLISH FAILED: ${event.moduleName}")

                publishModuleFailed(
                    moduleName = event.moduleName,
                    reason = failureReason
                )
            }
        }

        moduleEventBridgeListener = moduleListener
        ModuleEventBus.subscribe(moduleListener)
        println("MODULE BRIDGE INSTALLED, HAS LISTENERS=${ModuleEventBus.hasListeners()}")

        val failureListener: (RuntimeEvent) -> Unit = { event ->
            if (event is RuntimeEvent.ModuleFailed) {
                failureTracker.recordFailure(
                    reason = event.reason,
                    module = event.moduleName
                )
            }
        }

        runtimeFailureEventListener = failureListener
        RuntimeEventBus.subscribe(failureListener)

        markModuleEventBridgeInstalled()
    }

    override fun uninstallModuleEventBridge() {
        moduleEventBridgeListener?.let {
            ModuleEventBus.unsubscribe(it)
        }

        runtimeFailureEventListener?.let {
            RuntimeEventBus.unsubscribe(it)
        }

        moduleEventBridgeListener = null
        runtimeFailureEventListener = null

        resetRuntimeBridgeState()
    }

    override fun publishRuntimeStartedDiagnostic() {
        context().diagnosticEventBus.publish(
            CoreDiagnosticEvent(
                type = CoreDiagnosticEventType.RUNTIME_STARTED,
                snapshot = createDiagnosticSnapshot()
            )
        )
    }

    override fun publishRuntimeFailedDiagnostic() {
        context().diagnosticEventBus.publish(
            CoreDiagnosticEvent(
                type = CoreDiagnosticEventType.RUNTIME_FAILED,
                snapshot = createDiagnosticSnapshot()
            )
        )
    }

    override fun publishRuntimeStoppedDiagnostic() {
        context().diagnosticEventBus.publish(
            CoreDiagnosticEvent(
                type = CoreDiagnosticEventType.RUNTIME_STOPPED,
                snapshot = createDiagnosticSnapshot()
            )
        )
    }

    override fun healthProvider(): RuntimeHealthProvider {
        return healthProvider
    }

    override fun createHealthSnapshot(
        state: CoreRuntimeState,
        telemetry: RuntimeTelemetrySnapshot,
        failureReason: String?
    ): RuntimeHealthSnapshot {
        return healthProvider.createSnapshot(
            state = state,
            telemetry = telemetry,
            failureReason = failureReason
        )
    }

    override fun failureTracker(): RuntimeFailureTracker {
        return failureTracker
    }

    override fun recoveryTracker(): RuntimeRecoveryTracker {
        return recoveryTracker
    }

    override fun recordRuntimeFailure(
        reason: String,
        module: String
    ) {
        failureTracker.recordFailure(
            reason = reason,
            module = module
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
        controlRegistry.register(defaultRuntimeControl)
    }

    override fun commandHistoryProvider(): RuntimeCommandHistoryProvider {
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

    override fun runtimeServiceProviderHolder(): RuntimeServiceProviderHolder {
        return runtimeServiceProviderHolder
    }

    override fun setRuntimeServiceProvider(provider: RuntimeServiceProvider) {
        runtimeServiceProviderHolder.set(provider)
    }

    override fun runtimeServiceProvider(): RuntimeServiceProvider {
        return runtimeServiceProviderHolder.get()
            ?: error("RuntimeServiceProvider is not initialized")
    }

    override fun resetRuntimeServiceProvider() {
        runtimeServiceProviderHolder.reset()
    }

    override fun configureRuntimeServiceProvider(
        provider: RuntimeServiceProvider
    ) {
        setRuntimeServiceProvider(provider)
        setRuntimeServiceBootstrap(
            createServiceBootstrap(
                runtimeServiceProvider()
            )
        )
    }

    override fun resetRuntimeServiceConfiguration() {
        resetRuntimeServiceProvider()
        setRuntimeServiceBootstrap(
            createServiceBootstrap(
                runtimeServiceProvider()
            )
        )
    }


    override fun runtimeServiceRegistry(): RuntimeServiceRegistry {
        return runtimeServiceRegistry
    }

    override fun serviceBootstrap(): RuntimeServiceBootstrap {
        return serviceBootstrap
    }

    override fun runtimeServiceBootstrapHolder(): RuntimeServiceBootstrapHolder {
        return runtimeServiceBootstrapHolder
    }

    override fun setRuntimeServiceBootstrap(bootstrap: RuntimeServiceBootstrap) {
        runtimeServiceBootstrapHolder.set(bootstrap)
    }

    override fun runtimeServiceBootstrap(): RuntimeServiceBootstrap {
        return runtimeServiceBootstrapHolder.get()
            ?: error("RuntimeServiceBootstrap is not initialized")
    }


    override fun registerRuntimeService(
        service: RuntimeService
    ) {
        runtimeServiceBootstrapHolder.get().register(service)
    }

    override fun runtimeServiceStates(): Map<String, RuntimeServiceState> {
        return runtimeServiceBootstrapHolder.get().getStates()
    }

    override fun runtimeServiceFailures(): List<RuntimeServiceFailure> {
        return runtimeServiceBootstrapHolder.get().getFailures()
    }

    override fun runtimeServiceHealth(): Map<String, RuntimeServiceHealth> {
        return runtimeServiceBootstrapHolder.get().getHealth()
    }

    override fun runtimeServiceRecoverySnapshot(): RuntimeRecoverySnapshot? {
        return runtimeServiceBootstrapHolder.get().getRecoverySnapshot()
    }

    override fun startRuntimeServices() {
        runtimeServiceBootstrapHolder.get().start()
    }

    override fun stopRuntimeServices() {
        runtimeServiceBootstrapHolder.get().stop()
    }

    override fun createServiceBootstrap(
        provider: RuntimeServiceProvider
    ): RuntimeServiceBootstrap {
        return RuntimeServiceBootstrap(
            provider,
            runtimeServiceRegistry
        )
    }

    
    override fun runtimeMonitor(): RuntimeMonitor {
        return runtimeMonitor
    }

    override fun runtimeHealthSnapshot(): RuntimeHealthSnapshot {
        return createHealthSnapshot(
            state = runtimeState(),
            telemetry = telemetryObserver().snapshot(),
            failureReason = failureReason()
        )
    }

    override fun runtimeHealthReport(): RuntimeHealthReport {
        return createHealthReport(
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
                report = createHealthReport(
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
