package pro.liliya.core.runtime.composition

import pro.liliya.core.runtime.recovery.RuntimeRecoveryEventBus

import pro.liliya.core.logging.Logger
import pro.liliya.core.logging.LoggerFactory
import pro.liliya.core.logging.LogConfig


import pro.liliya.core.RuntimeEventBus
import pro.liliya.core.RuntimeEvent
import pro.liliya.core.ModuleEvent
import pro.liliya.core.ModuleEventBus

import pro.liliya.core.CoreDiagnosticSnapshot
import pro.liliya.core.CoreDiagnosticSource
import pro.liliya.core.CoreRuntimeStateHolder
import pro.liliya.core.CoreRuntimeState
import pro.liliya.core.CoreDiagnosticProvider
import pro.liliya.core.CoreRuntimeContext
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

import pro.liliya.core.runtime.observer.DefaultRuntimeObserverRegistry
import pro.liliya.core.runtime.observer.RuntimeObserverBridge
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEventBridge
import pro.liliya.core.runtime.module.ModuleEventBridge
import pro.liliya.core.runtime.policy.DefaultRuntimeActionPolicyEvaluator

import pro.liliya.core.module.CoreModuleProvider

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
import pro.liliya.core.runtime.dispatcher.RuntimeLifecycleActionHandler
import pro.liliya.core.runtime.dispatcher.RuntimeRestartActionHandler
import pro.liliya.core.runtime.dispatcher.RuntimeRecoverActionHandler

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
import pro.liliya.core.runtime.RuntimeObserverBridgeStateHolder
import pro.liliya.core.runtime.RuntimeModuleBridgeStateHolder
import pro.liliya.core.runtime.RuntimeRecoveryEventBridgeStateHolder
import pro.liliya.core.runtime.CoreRuntimeServiceProvider
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
import pro.liliya.core.runtime.intelligence.decision.DefaultRuntimeDecisionEngine
import pro.liliya.core.runtime.intelligence.decision.RuntimeDecisionEngine
import pro.liliya.core.runtime.intelligence.decision.DefaultRuntimeDecisionActionRequestFactory
import pro.liliya.core.runtime.intelligence.decision.RuntimeDecisionActionRequestFactory
import pro.liliya.core.runtime.intelligence.decision.explanation.DefaultRuntimeDecisionExplainer
import pro.liliya.core.runtime.intelligence.decision.explanation.RuntimeDecisionExplainer
import pro.liliya.core.runtime.intelligence.decision.reflection.DefaultRuntimeDecisionReflectionAnalyzer
import pro.liliya.core.runtime.intelligence.decision.reflection.RuntimeDecisionReflectionAnalyzer
import pro.liliya.core.runtime.intelligence.decision.quality.DefaultRuntimeDecisionQualityAssessor
import pro.liliya.core.runtime.intelligence.decision.quality.DefaultRuntimeDecisionQualityQuery
import pro.liliya.core.runtime.intelligence.decision.quality.RuntimeDecisionQualityAssessor
import pro.liliya.core.runtime.intelligence.decision.quality.RuntimeDecisionQualityQuery
import pro.liliya.core.runtime.intelligence.decision.quality.DefaultRuntimeDecisionQualityRecorder
import pro.liliya.core.runtime.intelligence.decision.quality.RuntimeDecisionQualityRecorder
import pro.liliya.core.runtime.intelligence.decision.quality.history.DefaultRuntimeDecisionQualityHistory
import pro.liliya.core.runtime.intelligence.decision.quality.history.RuntimeDecisionQualityHistory
import pro.liliya.core.runtime.intelligence.decision.quality.trend.DefaultRuntimeDecisionQualityTrendAnalyzer
import pro.liliya.core.runtime.intelligence.decision.quality.trend.DefaultRuntimeDecisionQualityTrendQuery
import pro.liliya.core.runtime.intelligence.decision.quality.trend.RuntimeDecisionQualityTrendAnalyzer
import pro.liliya.core.runtime.intelligence.decision.quality.trend.RuntimeDecisionQualityTrendQuery
import pro.liliya.core.runtime.intelligence.decision.reflection.DefaultRuntimeDecisionReflectionRecorder
import pro.liliya.core.runtime.intelligence.decision.reflection.RuntimeDecisionReflectionRecorder
import pro.liliya.core.runtime.intelligence.decision.reflection.history.DefaultRuntimeDecisionReflectionHistory
import pro.liliya.core.runtime.intelligence.decision.reflection.history.RuntimeDecisionReflectionHistory
import pro.liliya.core.runtime.intelligence.decision.reflection.trend.DefaultRuntimeDecisionReflectionTrendAnalyzer
import pro.liliya.core.runtime.intelligence.decision.reflection.trend.DefaultRuntimeDecisionReflectionTrendQuery
import pro.liliya.core.runtime.intelligence.decision.reflection.trend.RuntimeDecisionReflectionTrendAnalyzer
import pro.liliya.core.runtime.intelligence.decision.reflection.trend.RuntimeDecisionReflectionTrendQuery
import pro.liliya.core.runtime.intelligence.decision.explanation.DefaultRuntimeDecisionExplanationRecorder
import pro.liliya.core.runtime.intelligence.decision.explanation.RuntimeDecisionExplanationRecorder
import pro.liliya.core.runtime.intelligence.decision.explanation.history.DefaultRuntimeDecisionExplanationHistory
import pro.liliya.core.runtime.intelligence.decision.explanation.history.RuntimeDecisionExplanationHistory
import pro.liliya.core.runtime.intelligence.experience.composition.DefaultRuntimeExperienceComposition
import pro.liliya.core.runtime.intelligence.experience.composition.RuntimeExperienceComposition
import pro.liliya.core.runtime.intelligence.experience.knowledge.DefaultRuntimeExperienceKnowledgePipeline
import pro.liliya.core.runtime.intelligence.experience.knowledge.RuntimeExperienceKnowledgePipeline
import pro.liliya.core.runtime.intelligence.experience.orchestration.DefaultRuntimeExperienceKnowledgeOrchestrator
import pro.liliya.core.runtime.intelligence.experience.orchestration.RuntimeExperienceKnowledgeOrchestrator
import pro.liliya.core.runtime.intelligence.knowledge.composition.DefaultRuntimeKnowledgeComposition
import pro.liliya.core.runtime.intelligence.knowledge.composition.RuntimeKnowledgeComposition
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.composition.DefaultRuntimeKnowledgeLifecycleCompositionHolder
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.composition.RuntimeKnowledgeLifecycleComposition
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.composition.RuntimeKnowledgeLifecycleCompositionHolder
import pro.liliya.core.runtime.intelligence.memory.composition.DefaultRuntimeMemoryCompositionHolder
import pro.liliya.core.runtime.intelligence.memory.composition.RuntimeMemoryComposition
import pro.liliya.core.runtime.intelligence.memory.composition.RuntimeMemoryCompositionHolder
import pro.liliya.core.runtime.intelligence.memory.factory.DefaultRuntimeMemoryCompositionFactory
import pro.liliya.core.runtime.intelligence.memory.service.RuntimeMemoryService
import pro.liliya.core.runtime.intelligence.memory.lifecycle.RuntimeMemoryLifecycleController


import pro.liliya.core.runtime.intelligence.context.DefaultRuntimeContextProvider
import pro.liliya.core.runtime.intelligence.context.RuntimeContextMetadata
import pro.liliya.core.runtime.intelligence.selfmodel.DefaultRuntimeSelfModelProvider
import pro.liliya.core.runtime.intelligence.selfmodel.RuntimeSelfModelProvider
import pro.liliya.core.runtime.intelligence.reflection.DefaultRuntimeReflection
import pro.liliya.core.runtime.intelligence.reflection.RuntimeReflection
import pro.liliya.core.runtime.intelligence.reflection.history.DefaultRuntimeReflectionHistory
import pro.liliya.core.runtime.intelligence.reflection.history.RuntimeReflectionHistory
import pro.liliya.core.runtime.intelligence.reflection.trend.DefaultRuntimeReflectionTrendAnalyzer
import pro.liliya.core.runtime.intelligence.reflection.trend.RuntimeReflectionTrendAnalyzer
import pro.liliya.core.runtime.intelligence.meaning.DefaultRuntimeMeaningEngine
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningEngine
import pro.liliya.core.runtime.intelligence.orchestration.DefaultRuntimeIntelligenceOrchestrator
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.maintenance.DefaultRuntimeKnowledgeMaintenanceTrigger
import pro.liliya.core.runtime.intelligence.orchestration.RuntimeIntelligenceOrchestrator
import pro.liliya.core.runtime.intelligence.context.cognitive.composition.DefaultCognitiveContextComposition

import pro.liliya.core.runtime.intelligence.context.cognitive.composition.CognitiveContextComposition

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






    private val context = CoreRuntimeContext(
        diagnosticSource = diagnosticSource
    )


    private val runtimeStateHolder =
        CoreRuntimeStateHolder()

    private val runtimeObserverBridgeStateHolder =
        RuntimeObserverBridgeStateHolder()

    private val runtimeModuleBridgeStateHolder =
        RuntimeModuleBridgeStateHolder()
    private val runtimeRecoveryEventBridgeStateHolder =
        RuntimeRecoveryEventBridgeStateHolder()

    override val recoveryEventBus =
        RuntimeRecoveryEventBus()

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

    private val controlRegistry = RuntimeControlRegistry()

    private val commandHistoryProvider = RuntimeCommandHistoryProvider()

    private val actionAuditProvider = RuntimeActionAuditProvider()

    private val actionHandlerRegistry = RuntimeActionHandlerRegistry()

    private val defaultRuntimeControl =
        DefaultRuntimeControl(this)

    private val actionDispatcher =
        RuntimeActionDispatcher(
            actionHandlerRegistry,
            actionAuditProvider,
            actionPolicyEvaluator,
            this
        )

    private val runtimeActionExecutor =
        RuntimeActionExecutor(defaultRuntimeControl)

    private val healthRuntimeActionHandler =
        HealthRuntimeActionHandler(
            runtimeActionExecutor
        )

    private val lifecycleRuntimeActionHandler =
        RuntimeLifecycleActionHandler(
            runtimeActionExecutor
        )

    private val restartRuntimeActionHandler =
        RuntimeRestartActionHandler(
            runtimeActionExecutor
        )


    private val recoverRuntimeActionHandler =
        RuntimeRecoverActionHandler(
            runtimeActionExecutor
        )

    init {
        registerRuntimeActionHandlers()
    }

    private val lifecycleRecorder: RuntimeLifecycleRecorder =
        DefaultRuntimeLifecycleRecorder()

    private val lifecycleRecorderHolder =
        RuntimeLifecycleRecorderHolder(lifecycleRecorder)

    private val memoryCompositionHolder =
        DefaultRuntimeMemoryCompositionHolder(
            DefaultRuntimeMemoryCompositionFactory()
        )

    private val experienceComposition =
        DefaultRuntimeExperienceComposition()

    private val knowledgeComposition =
        DefaultRuntimeKnowledgeComposition()

    private val knowledgeLifecycleCompositionHolder =
        DefaultRuntimeKnowledgeLifecycleCompositionHolder(
            knowledgeMemory =
                memoryCompositionHolder
                    .composition()
                    .knowledgeMemory(),
            knowledgeLifecycleStateStore =
                memoryCompositionHolder
                    .composition()
                    .knowledgeLifecycleStateStore()
        )

    private val experienceKnowledgePipeline:
        RuntimeExperienceKnowledgePipeline =
        DefaultRuntimeExperienceKnowledgePipeline(
            experiencePipeline =
                experienceComposition.experiencePipeline(),
            experienceConsolidator =
                experienceComposition.experienceConsolidator(),
            knowledgePipeline =
                knowledgeComposition.knowledgePipeline(),
            experienceStore =
                experienceComposition.experienceStore(),
            knowledgeLifecycleMemory =
                knowledgeLifecycleCompositionHolder
                    .composition()
                    .lifecycleMemory()
        )

    private val experienceKnowledgeOrchestrator:
        RuntimeExperienceKnowledgeOrchestrator =
        DefaultRuntimeExperienceKnowledgeOrchestrator(
            pipeline = experienceKnowledgePipeline
        )











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

    private val runtimeRecoveryEventBridge =
        RuntimeRecoveryEventBridge(recoveryEventBus)

    private val telemetryObserver = RuntimeTelemetryObserver()

    private val healthReportProvider = RuntimeHealthReportProvider(
        runtimeHealthProvider
    )

    private val statusProvider = RuntimeStatusProvider()

    private val serviceProvider =
        CoreRuntimeServiceProvider()

    private var runtimeServiceRegistry =
        RuntimeServiceRegistry()

    private val runtimeContextProvider =
        DefaultRuntimeContextProvider(
            registry = runtimeServiceRegistry,
            runtimeStateHolder = runtimeStateHolder
        )

    private val cognitiveContextComposition =
        DefaultCognitiveContextComposition(
            runtimeContextProvider = runtimeContextProvider,
            knowledgeMemory =
                memoryCompositionHolder
                    .composition()
                    .knowledgeMemory()
        )

    private val selfModelProvider: RuntimeSelfModelProvider =
        DefaultRuntimeSelfModelProvider(
            contextProvider = runtimeContextProvider,
            metadataProvider = {
                RuntimeContextMetadata(
                    runtimeVersion = "unknown",
                    recoveryAvailable = true,
                    diagnosticsAvailable = true
                )
            }
        )

    private val reflection: RuntimeReflection =
        DefaultRuntimeReflection()

    private val reflectionHistory: RuntimeReflectionHistory =
        DefaultRuntimeReflectionHistory()

    private val trendAnalyzer: RuntimeReflectionTrendAnalyzer =
        DefaultRuntimeReflectionTrendAnalyzer()

    private val meaningEngine: RuntimeMeaningEngine =
        DefaultRuntimeMeaningEngine()

    private val decisionEngine: RuntimeDecisionEngine =
        DefaultRuntimeDecisionEngine()

    private val decisionExplainer: RuntimeDecisionExplainer =
        DefaultRuntimeDecisionExplainer(
            provenanceQuery =
                knowledgeLifecycleCompositionHolder
                    .composition()
                    .provenanceQuery(),
            provenanceIntegrityQuery =
                knowledgeLifecycleCompositionHolder
                    .composition()
                    .provenanceIntegrityQuery()
        )

    private val decisionReflectionAnalyzer:
        RuntimeDecisionReflectionAnalyzer =
        DefaultRuntimeDecisionReflectionAnalyzer()

    private val decisionReflectionHistory:
        RuntimeDecisionReflectionHistory =
        DefaultRuntimeDecisionReflectionHistory()

    private val decisionReflectionRecorder:
        RuntimeDecisionReflectionRecorder =
        DefaultRuntimeDecisionReflectionRecorder(
            analyzer = decisionReflectionAnalyzer,
            history = decisionReflectionHistory
        )

    private val decisionReflectionTrendAnalyzer:
        RuntimeDecisionReflectionTrendAnalyzer =
        DefaultRuntimeDecisionReflectionTrendAnalyzer()

    private val decisionReflectionTrendQuery:
        RuntimeDecisionReflectionTrendQuery =
        DefaultRuntimeDecisionReflectionTrendQuery(
            history = decisionReflectionHistory,
            analyzer = decisionReflectionTrendAnalyzer
        )

    private val decisionQualityAssessor:
        RuntimeDecisionQualityAssessor =
        DefaultRuntimeDecisionQualityAssessor()

    private val decisionQualityQuery:
        RuntimeDecisionQualityQuery =
        DefaultRuntimeDecisionQualityQuery(
            reflectionHistory = decisionReflectionHistory,
            trendQuery = decisionReflectionTrendQuery,
            assessor = decisionQualityAssessor
        )

    private val decisionQualityHistory:
        RuntimeDecisionQualityHistory =
        DefaultRuntimeDecisionQualityHistory()

    private val decisionQualityRecorder:
        RuntimeDecisionQualityRecorder =
        DefaultRuntimeDecisionQualityRecorder(
            query = decisionQualityQuery,
            history = decisionQualityHistory
        )

    private val decisionQualityTrendAnalyzer:
        RuntimeDecisionQualityTrendAnalyzer =
        DefaultRuntimeDecisionQualityTrendAnalyzer()

    private val decisionQualityTrendQuery:
        RuntimeDecisionQualityTrendQuery =
        DefaultRuntimeDecisionQualityTrendQuery(
            history = decisionQualityHistory,
            analyzer = decisionQualityTrendAnalyzer
        )

    private val decisionExplanationHistory:
        RuntimeDecisionExplanationHistory =
        DefaultRuntimeDecisionExplanationHistory()

    private val decisionExplanationRecorder:
        RuntimeDecisionExplanationRecorder =
        DefaultRuntimeDecisionExplanationRecorder(
            explainer = decisionExplainer,
            history = decisionExplanationHistory
        )

    private val decisionActionRequestFactory:
        RuntimeDecisionActionRequestFactory =
        DefaultRuntimeDecisionActionRequestFactory()

    private val intelligenceOrchestrator: RuntimeIntelligenceOrchestrator =
        DefaultRuntimeIntelligenceOrchestrator(
            selfModelProvider = selfModelProvider,
            reflection = reflection,
            reflectionHistory = reflectionHistory,
            trendAnalyzer = trendAnalyzer,
            meaningEngine = meaningEngine,
            experienceKnowledgeOrchestrator =
                experienceKnowledgeOrchestrator,
            cognitiveContext =
                cognitiveContextComposition.context(),
            knowledgeMaintenanceTrigger =
                DefaultRuntimeKnowledgeMaintenanceTrigger(
                    knowledgeLifecycleCompositionHolder
                        .composition()
                        .maintenanceService()
                )
        )

    private val runtimeSupervisor =
        RuntimeSupervisor(
            registryProvider = { runtimeServiceRegistry }
        )

    private val runtimeRecoveryManager =
        RuntimeRecoveryManager(runtimeSupervisor, runtimeServiceRegistry, recoveryEventBus)

    private val runtimeServiceProviderHolder =
        RuntimeServiceProviderHolder(
            serviceProvider
        )

    private val runtimeServiceBootstrapHolder =
        RuntimeServiceBootstrapHolder {
            createServiceBootstrap(
                runtimeServiceProvider()
            )
        }






    private val moduleProvider: ModuleProvider =
        CoreModuleProvider()

    private val moduleProviderHolder =
        ModuleProviderHolder(moduleProvider)

    private val moduleManagerHolder =
        ModuleManagerHolder()

    private val moduleExceptionHandler =
        ModuleExceptionHandler()

    private val moduleDependencyResolver =
        ModuleDependencyResolver()

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
        return createModuleManager(
            registry = createModuleRegistry(),
            provider = moduleProviderHolder.get()
        )
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

        memoryComposition()
            .lifecycle()
            .reset()

        cognitiveContextComposition
            .lifecycle()
            .reset()

        setRuntimeState(CoreRuntimeState.STARTING)
    }

    override fun startRuntimeLifecycle(): ModuleManager {

        memoryComposition()
            .lifecycle()
            .start()

        cognitiveContextComposition
            .lifecycle()
            .start()

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

        memoryComposition()
            .lifecycle()
            .stop()

        cognitiveContextComposition
            .lifecycle()
            .stop()

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
        installRuntimeRecoveryEventBridge()

        publishSystemStart()
        prepareRuntimeStartup()
        publishRuntimeStarting()

        try {
            startRuntimeLifecycle()
            recordRuntimeStarted()
            markRuntimeRecovered()
            publishRuntimeReady()
            logRuntimeStartupSuccess()
        } catch (error: Exception) {
            moduleManager()?.let {
                try {
                    stopModuleRuntime(it)
                } catch (cleanupError: Exception) {
                    logger().error(
                        pro.liliya.core.logging.LoggerMarkers.ERROR,
                        "Runtime startup cleanup failed: ${cleanupError.message}",
                        cleanupError
                    )
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

        stopRuntimeBridges()

        stopRuntimeLifecycle()

        recordRuntimeStopped()

        setFailureReason(null)
        setModuleStates(emptyMap())

        publishSystemStop()

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

    override fun installModuleProvider(
        provider: ModuleProvider
    ) {
        setModuleProvider(provider)
    }

    override fun removeModuleProvider() {
        resetModuleProvider()
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

    override fun runtimeObserverBridgeStateHolder(): RuntimeObserverBridgeStateHolder {
        return runtimeObserverBridgeStateHolder
    }

    override fun runtimeModuleBridgeStateHolder(): RuntimeModuleBridgeStateHolder {
        return runtimeModuleBridgeStateHolder
    }

    override fun runtimeRecoveryEventBridgeStateHolder(): RuntimeRecoveryEventBridgeStateHolder {
        return runtimeRecoveryEventBridgeStateHolder
    }

    override fun isRuntimeObserverBridgeInstalled(): Boolean {
        return runtimeObserverBridgeStateHolder.isRuntimeObserverBridgeInstalled()
    }

    override fun markRuntimeObserverBridgeInstalled() {
        runtimeObserverBridgeStateHolder.markRuntimeObserverBridgeInstalled()
    }

    override fun resetRuntimeObserverBridgeState() {
        runtimeObserverBridgeStateHolder.reset()
    }

    override fun stopRuntimeBridges() {
        uninstallRuntimeRecoveryEventBridge()
        uninstallModuleEventBridge()
        uninstallRuntimeObserverBridge()

        
        resetRuntimeObserverBridgeState()
    }


    override fun moduleProvider(): ModuleProvider {
        return moduleProviderHolder.get()
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

            observerRegistry.subscribe(
                telemetryObserver()
            )
        }
    }


    fun uninstallRuntimeObserverBridge() {
        observerRegistry.unsubscribe(
            telemetryObserver()
        )

        observerBridge.uninstall()

        resetRuntimeObserverBridgeState()
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

    override fun experienceComposition():
        RuntimeExperienceComposition {
        return experienceComposition
    }

    override fun knowledgeComposition():
        RuntimeKnowledgeComposition {
        return knowledgeComposition
    }

    override fun experienceKnowledgePipeline():
        RuntimeExperienceKnowledgePipeline {
        return experienceKnowledgePipeline
    }

    override fun experienceKnowledgeOrchestrator():
        RuntimeExperienceKnowledgeOrchestrator {
        return experienceKnowledgeOrchestrator
    }

    override fun intelligenceOrchestrator():
        RuntimeIntelligenceOrchestrator {
        return intelligenceOrchestrator
    }

    override fun decisionEngine(): RuntimeDecisionEngine {
        return decisionEngine
    }

    override fun decisionExplainer(): RuntimeDecisionExplainer {
        return decisionExplainer
    }

    override fun decisionReflectionAnalyzer():
        RuntimeDecisionReflectionAnalyzer {
        return decisionReflectionAnalyzer
    }

    override fun decisionReflectionHistory():
        RuntimeDecisionReflectionHistory {
        return decisionReflectionHistory
    }

    override fun decisionReflectionRecorder():
        RuntimeDecisionReflectionRecorder {
        return decisionReflectionRecorder
    }

    override fun decisionReflectionTrendAnalyzer():
        RuntimeDecisionReflectionTrendAnalyzer {
        return decisionReflectionTrendAnalyzer
    }

    override fun decisionReflectionTrendQuery():
        RuntimeDecisionReflectionTrendQuery {
        return decisionReflectionTrendQuery
    }

    override fun decisionQualityAssessor():
        RuntimeDecisionQualityAssessor {
        return decisionQualityAssessor
    }

    override fun decisionQualityQuery():
        RuntimeDecisionQualityQuery {
        return decisionQualityQuery
    }

    override fun decisionQualityHistory():
        RuntimeDecisionQualityHistory {
        return decisionQualityHistory
    }

    override fun decisionQualityRecorder():
        RuntimeDecisionQualityRecorder {
        return decisionQualityRecorder
    }

    override fun decisionQualityTrendAnalyzer():
        RuntimeDecisionQualityTrendAnalyzer {
        return decisionQualityTrendAnalyzer
    }

    override fun decisionQualityTrendQuery():
        RuntimeDecisionQualityTrendQuery {
        return decisionQualityTrendQuery
    }

    override fun decisionExplanationHistory():
        RuntimeDecisionExplanationHistory {
        return decisionExplanationHistory
    }

    override fun decisionExplanationRecorder():
        RuntimeDecisionExplanationRecorder {
        return decisionExplanationRecorder
    }

    override fun decisionActionRequestFactory():
        RuntimeDecisionActionRequestFactory {
        return decisionActionRequestFactory
    }


    override fun knowledgeLifecycleComposition():
        RuntimeKnowledgeLifecycleComposition {
        return knowledgeLifecycleCompositionHolder.composition()
    }

    override fun knowledgeLifecycleCompositionHolder():
        RuntimeKnowledgeLifecycleCompositionHolder {
        return knowledgeLifecycleCompositionHolder
    }

    override fun memoryComposition():
        RuntimeMemoryComposition {
        return memoryCompositionHolder.composition()
    }

    override fun memoryCompositionHolder():
        RuntimeMemoryCompositionHolder {
        return memoryCompositionHolder
    }
    override fun memoryService():
        RuntimeMemoryService {
        return memoryComposition()
            .service()
    }

    override fun memoryLifecycle():
        RuntimeMemoryLifecycleController {
        return memoryComposition()
            .lifecycle()
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

    fun installRuntimeRecoveryEventBridge() {
        if (!runtimeRecoveryEventBridgeStateHolder.isRuntimeRecoveryEventBridgeInstalled()) {
            runtimeRecoveryManager.install()
            runtimeRecoveryEventBridge.install()
            runtimeRecoveryEventBridgeStateHolder.markRuntimeRecoveryEventBridgeInstalled()
        }
    }

    fun uninstallRuntimeRecoveryEventBridge() {
        runtimeRecoveryManager.uninstall()
        runtimeRecoveryEventBridge.uninstall()
        runtimeRecoveryEventBridgeStateHolder.reset()
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

    override fun resetRuntimeHealth() {
        telemetryObserver.reset()
        failureTracker.clear()
        recoveryTracker.clear()
        runtimeRecoveryManager.reset()
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
        controlRegistry
            .register(defaultRuntimeControl)
    }

    override fun commandHistoryProvider(): RuntimeCommandHistoryProvider {
        return commandHistoryProvider
    }

    override fun actionAuditProvider(): RuntimeActionAuditProvider {
        return actionAuditProvider
    }

    private fun resetRuntimeHistory() {
        commandHistoryProvider.clear()
        actionAuditProvider.clear()
    }

    override fun registerRuntimeActionHandlers() {
        actionHandlerRegistry
            .register(healthRuntimeActionHandler)

        actionHandlerRegistry
            .register(lifecycleRuntimeActionHandler)

        actionHandlerRegistry
            .register(restartRuntimeActionHandler)

        actionHandlerRegistry
            .register(recoverRuntimeActionHandler)
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
        runtimeServiceBootstrapHolder
            .get()
            .stop()

        runtimeServiceProviderHolder.set(provider)

        runtimeServiceBootstrapHolder.replace(
            createServiceBootstrap(
                runtimeServiceProvider()
            )
        )
    }

    override fun resetRuntimeServiceConfiguration() {
        val provider = runtimeServiceProvider()

        runtimeServiceBootstrapHolder
            .get()
            .stop()

        runtimeServiceBootstrapHolder.replace(
            createServiceBootstrap(provider)
        )
    }


    override fun prepareRuntime() {
        decisionQualityHistory.clear()
        decisionReflectionHistory.clear()
        decisionExplanationHistory.clear()
        resetRuntimeServiceConfiguration()
        resetRuntimeServiceProvider()
        clearModuleManager()
        resetModuleProvider()
        resetRuntimeState()
        stopRuntimeBridges()
        resetRuntimeHealth()
        runtimeSupervisor.reset()
        resetRuntimeControlState()
        resetRuntimeCapabilities()
        lifecycleRecorder.reset()
        resetRuntimeHistory()
        cognitiveContextComposition
            .lifecycle()
            .reset()
        knowledgeLifecycleCompositionHolder
            .composition()
            .reset()
        resetRuntimeServiceRegistry()
    }

    private fun resetRuntimeControlState() {
        controlRegistry.clear()
        commandHistoryProvider.clear()
        actionAuditProvider.clear()
        actionHandlerRegistry.clear()
    }

    private fun resetRuntimeCapabilities() {
        capabilityInfrastructure
            .lifecycleManager()
            .reset()
    }

    private fun resetRuntimeServiceRegistry() {
        runtimeServiceRegistry.reset()
    }


    override fun runtimeServiceRegistry(): RuntimeServiceRegistry {
        return runtimeServiceRegistry
    }

    override fun cognitiveContextComposition():
        CognitiveContextComposition {
        return cognitiveContextComposition
    }


    override fun serviceBootstrap(): RuntimeServiceBootstrap {
        return runtimeServiceBootstrapHolder.get()
    }

    override fun runtimeServiceBootstrapHolder(): RuntimeServiceBootstrapHolder {
        return runtimeServiceBootstrapHolder
    }

    override fun replaceRuntimeServiceBootstrap(bootstrap: RuntimeServiceBootstrap) {
        runtimeServiceBootstrapHolder.replace(bootstrap)
    }

    fun runtimeSupervisor(): RuntimeSupervisor {
        return runtimeSupervisor
    }

    fun createServiceBootstrap(
        provider: RuntimeServiceProvider
    ): RuntimeServiceBootstrap {
        return RuntimeServiceBootstrap(
            runtimeServiceProviderHolder,
            runtimeServiceRegistry,
            runtimeSupervisor,
            runtimeRecoveryManager
        )
    }

    override fun runtimeRecoveryManager(): RuntimeRecoveryManager {
        return runtimeRecoveryManager
    }


    override fun registerRuntimeService(
        service: RuntimeService
    ) {
        serviceBootstrap().register(service)
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
            runtimeStatusSnapshot = runtimeStatusSnapshot(),
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
