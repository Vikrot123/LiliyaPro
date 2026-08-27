package pro.liliya.core.runtime.composition

import pro.liliya.core.runtime.recovery.RuntimeRecoveryEventBus

import pro.liliya.core.logging.Logger

import pro.liliya.core.CoreDiagnosticSnapshot
import pro.liliya.core.CoreDiagnosticSource
import pro.liliya.core.CoreRuntimeStateHolder
import pro.liliya.core.CoreRuntimeState
import pro.liliya.core.CoreRuntimeContext
import pro.liliya.core.module.ModuleRegistry
import pro.liliya.core.module.ModuleProviderHolder
import pro.liliya.core.module.ModuleProvider
import pro.liliya.core.module.ModuleExceptionHandler
import pro.liliya.core.module.ModuleDependencyResolver
import pro.liliya.core.module.ModuleManager
import pro.liliya.core.module.ModuleManagerHolder
import pro.liliya.core.module.ModuleState
import pro.liliya.core.runtime.lifecycle.RuntimeLifecycleRecorder
import pro.liliya.core.runtime.lifecycle.RuntimeLifecycleRecorderHolder
import pro.liliya.core.runtime.intelligence.experience.composition.RuntimeExperienceComposition
import pro.liliya.core.runtime.intelligence.experience.knowledge.RuntimeExperienceKnowledgePipeline
import pro.liliya.core.runtime.intelligence.experience.orchestration.RuntimeExperienceKnowledgeOrchestrator
import pro.liliya.core.runtime.intelligence.knowledge.composition.RuntimeKnowledgeComposition
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.composition.RuntimeKnowledgeLifecycleComposition
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.composition.RuntimeKnowledgeLifecycleCompositionHolder
import pro.liliya.core.runtime.intelligence.memory.composition.RuntimeMemoryComposition
import pro.liliya.core.runtime.intelligence.memory.composition.RuntimeMemoryCompositionHolder
import pro.liliya.core.runtime.intelligence.memory.service.RuntimeMemoryService
import pro.liliya.core.runtime.intelligence.memory.lifecycle.RuntimeMemoryLifecycleController


import pro.liliya.core.runtime.observer.RuntimeObserverBridge
import pro.liliya.core.runtime.observer.DefaultRuntimeObserverRegistry
import pro.liliya.core.runtime.policy.RuntimeActionPolicyEvaluator
import pro.liliya.core.runtime.capability.RuntimeCapabilityRegistry
import pro.liliya.core.runtime.capability.RuntimeCapabilityInfrastructure
import pro.liliya.core.runtime.capability.RuntimeCapabilityAuthorityEvaluator
import pro.liliya.core.runtime.capability.RuntimeCapabilityResolver
import pro.liliya.core.runtime.health.RuntimeHealthProvider
import pro.liliya.core.runtime.health.RuntimeFailureTracker
import pro.liliya.core.runtime.health.RuntimeRecoveryTracker
import pro.liliya.core.runtime.health.RuntimeHealthReportProvider
import pro.liliya.core.runtime.health.RuntimeHealthReport
import pro.liliya.core.runtime.status.RuntimeStatusSnapshot
import pro.liliya.core.runtime.health.RuntimeFailureHealthSnapshot
import pro.liliya.core.runtime.status.RuntimeStatusProvider
import pro.liliya.core.runtime.monitor.RuntimeMonitor
import pro.liliya.core.runtime.telemetry.RuntimeTelemetryObserver
import pro.liliya.core.runtime.control.RuntimeControl
import pro.liliya.core.runtime.history.RuntimeCommandHistoryProvider
import pro.liliya.core.runtime.audit.RuntimeActionAuditProvider
import pro.liliya.core.runtime.dispatcher.RuntimeActionDispatcher
import pro.liliya.core.runtime.RuntimeObserverBridgeStateHolder
import pro.liliya.core.runtime.RuntimeModuleBridgeStateHolder
import pro.liliya.core.runtime.RuntimeRecoveryEventBridgeStateHolder
import pro.liliya.core.runtime.RuntimeServiceBootstrap
import pro.liliya.core.runtime.RuntimeServiceRegistry
import pro.liliya.core.runtime.RuntimeServiceBootstrapHolder
import pro.liliya.core.runtime.RuntimeServiceProvider
import pro.liliya.core.runtime.RuntimeServiceProviderHolder
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.RuntimeServiceFailure
import pro.liliya.core.runtime.RuntimeServiceHealth
import pro.liliya.core.runtime.RuntimeRecoveryManager
import pro.liliya.core.runtime.RuntimeRecoverySnapshot
import pro.liliya.core.runtime.health.RuntimeRecoverySnapshot as HealthRecoverySnapshot
import pro.liliya.core.runtime.health.RuntimeHealthSnapshot
import pro.liliya.core.runtime.telemetry.RuntimeTelemetrySnapshot

import pro.liliya.core.runtime.intelligence.context.cognitive.composition.CognitiveContextComposition
import pro.liliya.core.runtime.intelligence.orchestration.RuntimeIntelligenceOrchestrator
import pro.liliya.core.runtime.intelligence.decision.RuntimeDecisionEngine
import pro.liliya.core.runtime.intelligence.goal.RuntimeGoalDeriver
import pro.liliya.core.runtime.intelligence.planning.RuntimePlanner
import pro.liliya.core.runtime.intelligence.reasoning.RuntimeReasoningAnalyzer
import pro.liliya.core.runtime.intelligence.decision.RuntimeDecisionActionRequestFactory
import pro.liliya.core.runtime.intelligence.decision.explanation.RuntimeDecisionExplainer
import pro.liliya.core.runtime.intelligence.decision.reflection.RuntimeDecisionReflectionAnalyzer
import pro.liliya.core.runtime.intelligence.decision.quality.RuntimeDecisionQualityAssessor
import pro.liliya.core.runtime.intelligence.decision.quality.RuntimeDecisionQualityQuery
import pro.liliya.core.runtime.intelligence.decision.quality.RuntimeDecisionQualityRecorder
import pro.liliya.core.runtime.intelligence.decision.quality.history.RuntimeDecisionQualityHistory
import pro.liliya.core.runtime.intelligence.decision.quality.trend.RuntimeDecisionQualityTrendAnalyzer
import pro.liliya.core.runtime.intelligence.decision.quality.trend.RuntimeDecisionQualityTrendQuery
import pro.liliya.core.runtime.intelligence.decision.quality.summary.RuntimeDecisionQualitySummaryQuery
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.RuntimeDecisionQualityAdvisoryQuery
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.RuntimeDecisionQualityAdvisoryRecorder
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.history.RuntimeDecisionQualityAdvisoryHistory
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.trend.RuntimeDecisionQualityAdvisoryTrendAnalyzer
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.trend.RuntimeDecisionQualityAdvisoryTrendQuery
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.summary.RuntimeDecisionQualityAdvisorySummaryQuery
import pro.liliya.core.runtime.intelligence.decision.quality.governance.RuntimeDecisionQualityGovernanceQuery
import pro.liliya.core.runtime.intelligence.decision.quality.governance.RuntimeDecisionQualityGovernanceRecorder
import pro.liliya.core.runtime.intelligence.decision.quality.governance.history.RuntimeDecisionQualityGovernanceHistory
import pro.liliya.core.runtime.intelligence.decision.quality.governance.trend.RuntimeDecisionQualityGovernanceTrendAnalyzer
import pro.liliya.core.runtime.intelligence.decision.quality.governance.trend.RuntimeDecisionQualityGovernanceTrendQuery
import pro.liliya.core.runtime.intelligence.decision.reflection.RuntimeDecisionReflectionRecorder
import pro.liliya.core.runtime.intelligence.decision.reflection.history.RuntimeDecisionReflectionHistory
import pro.liliya.core.runtime.intelligence.decision.reflection.trend.RuntimeDecisionReflectionTrendAnalyzer
import pro.liliya.core.runtime.intelligence.decision.reflection.trend.RuntimeDecisionReflectionTrendQuery
import pro.liliya.core.runtime.intelligence.decision.explanation.RuntimeDecisionExplanationRecorder
import pro.liliya.core.runtime.intelligence.decision.explanation.history.RuntimeDecisionExplanationHistory

interface RuntimeComposition {
    
    val recoveryEventBus: RuntimeRecoveryEventBus


    fun cognitiveContextComposition(): CognitiveContextComposition

    fun intelligenceOrchestrator(): RuntimeIntelligenceOrchestrator

    fun goalDeriver(): RuntimeGoalDeriver
    fun planner(): RuntimePlanner
    fun reasoningAnalyzer(): RuntimeReasoningAnalyzer

    fun decisionEngine(): RuntimeDecisionEngine
    fun decisionExplainer(): RuntimeDecisionExplainer
    fun decisionReflectionAnalyzer(): RuntimeDecisionReflectionAnalyzer
    fun decisionReflectionHistory(): RuntimeDecisionReflectionHistory
    fun decisionReflectionRecorder(): RuntimeDecisionReflectionRecorder
    fun decisionReflectionTrendAnalyzer(): RuntimeDecisionReflectionTrendAnalyzer
    fun decisionReflectionTrendQuery(): RuntimeDecisionReflectionTrendQuery
    fun decisionQualityAssessor(): RuntimeDecisionQualityAssessor
    fun decisionQualityQuery(): RuntimeDecisionQualityQuery
    fun decisionQualityHistory(): RuntimeDecisionQualityHistory
    fun decisionQualityRecorder(): RuntimeDecisionQualityRecorder
    fun decisionQualityTrendAnalyzer(): RuntimeDecisionQualityTrendAnalyzer
    fun decisionQualityTrendQuery(): RuntimeDecisionQualityTrendQuery
    fun decisionQualitySummaryQuery(): RuntimeDecisionQualitySummaryQuery
    fun decisionQualityAdvisoryQuery(): RuntimeDecisionQualityAdvisoryQuery
    fun decisionQualityAdvisoryHistory(): RuntimeDecisionQualityAdvisoryHistory
    fun decisionQualityAdvisoryRecorder(): RuntimeDecisionQualityAdvisoryRecorder
    fun decisionQualityAdvisoryTrendAnalyzer(): RuntimeDecisionQualityAdvisoryTrendAnalyzer
    fun decisionQualityAdvisoryTrendQuery(): RuntimeDecisionQualityAdvisoryTrendQuery
    fun decisionQualityAdvisorySummaryQuery(): RuntimeDecisionQualityAdvisorySummaryQuery
    fun decisionQualityGovernanceQuery(): RuntimeDecisionQualityGovernanceQuery
    fun decisionQualityGovernanceHistory(): RuntimeDecisionQualityGovernanceHistory
    fun decisionQualityGovernanceRecorder(): RuntimeDecisionQualityGovernanceRecorder
    fun decisionQualityGovernanceTrendAnalyzer(): RuntimeDecisionQualityGovernanceTrendAnalyzer
    fun decisionQualityGovernanceTrendQuery(): RuntimeDecisionQualityGovernanceTrendQuery
    fun decisionExplanationHistory(): RuntimeDecisionExplanationHistory
    fun decisionExplanationRecorder(): RuntimeDecisionExplanationRecorder
    fun decisionActionRequestFactory(): RuntimeDecisionActionRequestFactory

    fun context(): CoreRuntimeContext
    fun diagnosticSource(): CoreDiagnosticSource
    fun logger(): Logger

    fun createModuleRegistry(): ModuleRegistry
    fun moduleManagerHolder(): ModuleManagerHolder
    fun moduleManager(): ModuleManager?
    fun setModuleManager(manager: ModuleManager)
    fun clearModuleManager()

    fun runtimeStateHolder(): CoreRuntimeStateHolder

    fun runtimeState(): CoreRuntimeState
    fun setRuntimeState(state: CoreRuntimeState)
    fun failureReason(): String?
    fun setFailureReason(reason: String?)

    fun markRuntimeFailed(reason: String)

    fun moduleStates(): Map<String, ModuleState>
    fun setModuleStates(states: Map<String, ModuleState>)

    fun runtimeObserverBridgeStateHolder(): RuntimeObserverBridgeStateHolder
    fun runtimeModuleBridgeStateHolder(): RuntimeModuleBridgeStateHolder
    fun runtimeRecoveryEventBridgeStateHolder(): RuntimeRecoveryEventBridgeStateHolder
    fun isRuntimeObserverBridgeInstalled(): Boolean
    fun markRuntimeObserverBridgeInstalled()
    fun resetRuntimeObserverBridgeState()

    fun stopRuntimeBridges()


    fun createModuleManager(
        registry: ModuleRegistry,
        provider: pro.liliya.core.module.ModuleProvider
    ): ModuleManager

    fun createModuleRuntime(): ModuleManager

    fun startRuntimeComponents(): ModuleManager

    fun prepareRuntimeStartup()
    fun startRuntimeLifecycle(): ModuleManager

    fun stopRuntimeLifecycle()



    fun startRuntime()
    fun stopRuntime()

    fun startLifecycle()

    fun stopLifecycle()

    fun handleRuntimeStartupFailure(
        error: Exception
    )

    fun logRuntimeStartupSuccess()

    fun logRuntimeStartupFailure(
        error: Exception
    )

    fun logRuntimeStopped()

    fun resetRuntimeState()

    fun startModuleRuntime(
        manager: ModuleManager
    )
    fun stopModuleRuntime(
        manager: ModuleManager
    )
    fun clearModuleRuntime()
    fun moduleProvider(): pro.liliya.core.module.ModuleProvider
    fun moduleProviderHolder(): ModuleProviderHolder

    fun setModuleProvider(
        provider: ModuleProvider
    )

    fun resetModuleProvider()

    fun installModuleProvider(
        provider: ModuleProvider
    )

    fun removeModuleProvider()
    fun createDiagnosticSnapshot(): CoreDiagnosticSnapshot

    fun observerRegistry(): DefaultRuntimeObserverRegistry

    fun observerBridge(): RuntimeObserverBridge
    fun installRuntimeObserverBridge()

    fun actionPolicyEvaluator(): RuntimeActionPolicyEvaluator

    fun capabilityRegistry(): RuntimeCapabilityRegistry
    fun capabilityInfrastructure(): RuntimeCapabilityInfrastructure
    fun moduleExceptionHandler(): ModuleExceptionHandler
    fun moduleDependencyResolver(): ModuleDependencyResolver

    fun capabilityAuthorityEvaluator(): RuntimeCapabilityAuthorityEvaluator

    fun capabilityResolver(): RuntimeCapabilityResolver

    fun lifecycleRecorder(): RuntimeLifecycleRecorder

    fun lifecycleRecorderHolder(): RuntimeLifecycleRecorderHolder
    
    fun knowledgeLifecycleComposition():
        RuntimeKnowledgeLifecycleComposition

    fun knowledgeLifecycleCompositionHolder():
        RuntimeKnowledgeLifecycleCompositionHolder

    fun experienceComposition():
        RuntimeExperienceComposition

    fun experienceKnowledgePipeline():
        RuntimeExperienceKnowledgePipeline

    fun experienceKnowledgeOrchestrator():
        RuntimeExperienceKnowledgeOrchestrator

    fun knowledgeComposition():
        RuntimeKnowledgeComposition

    fun memoryComposition():
        RuntimeMemoryComposition

    fun memoryCompositionHolder():
        RuntimeMemoryCompositionHolder

    fun memoryService():
        RuntimeMemoryService

    fun memoryLifecycle():
        RuntimeMemoryLifecycleController


    fun recordRuntimeStarted()
    fun recordRuntimeStopped()
    fun recordRuntimeFailure(reason: String?)

    fun healthProvider(): RuntimeHealthProvider
    fun createHealthSnapshot(
        state: CoreRuntimeState,
        telemetry: RuntimeTelemetrySnapshot,
        failureReason: String?
    ): RuntimeHealthSnapshot

    fun runtimeHealthSnapshot(): RuntimeHealthSnapshot


    fun failureTracker(): RuntimeFailureTracker

    fun runtimeRecoveryManager(): RuntimeRecoveryManager

    fun recoveryTracker(): RuntimeRecoveryTracker

    fun recordRuntimeFailure(
        reason: String,
        module: String
    )

    fun publishSystemStart()

    fun publishRuntimeStarting()

    fun publishRuntimeReady()

    fun publishRuntimeFailed(reason: String)

    fun publishSystemStop()

    fun publishModuleFailed(
        moduleName: String,
        reason: String
    )

    fun installModuleEventBridge()

    fun uninstallModuleEventBridge()

    fun resetRuntimeHealth()

    fun markRuntimeRecovered()

    fun healthReportProvider(): RuntimeHealthReportProvider
    fun createHealthReport(
        state: CoreRuntimeState,
        telemetry: RuntimeTelemetrySnapshot,
        failure: RuntimeFailureHealthSnapshot,
        recovery: HealthRecoverySnapshot
    ): RuntimeHealthReport

    fun runtimeHealthReport(): RuntimeHealthReport


    fun statusProvider(): RuntimeStatusProvider

    fun createRuntimeStatus(
        report: RuntimeHealthReport
    ): RuntimeStatusSnapshot

    fun runtimeStatusSnapshot(): RuntimeStatusSnapshot

    fun runtimeMonitor(): RuntimeMonitor

    fun telemetryObserver(): RuntimeTelemetryObserver

    fun registerRuntimeControls()

    fun commandHistoryProvider(): RuntimeCommandHistoryProvider

    fun actionAuditProvider(): RuntimeActionAuditProvider

    fun registerRuntimeActionHandlers()

    fun actionDispatcher(): RuntimeActionDispatcher

    fun defaultRuntimeControl(): RuntimeControl
    fun serviceProvider(): RuntimeServiceProvider
    fun runtimeServiceProviderHolder(): RuntimeServiceProviderHolder
    fun runtimeServiceProvider(): RuntimeServiceProvider

    fun setRuntimeServiceProvider(
        provider: RuntimeServiceProvider
    )

    fun resetRuntimeServiceProvider()

    fun replaceRuntimeServiceBootstrap(
        bootstrap: RuntimeServiceBootstrap
    )

    fun configureRuntimeServiceProvider(
        provider: RuntimeServiceProvider
    )

    fun resetRuntimeServiceConfiguration()

    fun prepareRuntime()

    fun runtimeServiceRegistry(): RuntimeServiceRegistry
    fun serviceBootstrap(): RuntimeServiceBootstrap
    fun runtimeServiceBootstrapHolder(): RuntimeServiceBootstrapHolder


    fun registerRuntimeService(
        service: RuntimeService
    )

    fun runtimeServiceStates(): Map<String, RuntimeServiceState>
    fun runtimeServiceFailures(): List<RuntimeServiceFailure>
    fun runtimeServiceHealth(): Map<String, RuntimeServiceHealth>
    fun runtimeServiceRecoverySnapshot(): RuntimeRecoverySnapshot?


}
