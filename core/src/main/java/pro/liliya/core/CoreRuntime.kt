package pro.liliya.core

import pro.liliya.core.logging.LogConfig
import pro.liliya.core.logging.Logger
import pro.liliya.core.logging.LoggerFactory
import pro.liliya.core.module.CoreModuleProvider
import pro.liliya.core.module.ModuleManager
import pro.liliya.core.module.ModuleRegistry
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceRegistry
import pro.liliya.core.runtime.RuntimeServiceProvider
import pro.liliya.core.runtime.CoreRuntimeServiceProvider
import pro.liliya.core.runtime.RuntimeServiceBootstrap

object CoreRuntime {

    private val context = CoreRuntimeContext()


    private var moduleManager: ModuleManager? = null

    private var moduleProvider: pro.liliya.core.module.ModuleProvider =
        CoreModuleProvider()

    private var runtimeState = CoreRuntimeState.STOPPED

    private var lastFailureReason: String? = null

    private var lastModuleStates: Map<String, pro.liliya.core.module.ModuleState> =
        emptyMap()

    private var moduleEventBridgeInstalled = false

private var runtimeServiceBootstrap =
    RuntimeServiceBootstrap(
        CoreRuntimeServiceProvider(),
        RuntimeServiceRegistry()
    )


    private var runtimeServiceProvider: RuntimeServiceProvider =
        CoreRuntimeServiceProvider()

    private val diagnosticEventBus = context.diagnosticEventBus

    private val diagnosticService =
        CoreRuntimeDiagnosticService(
            CoreRuntimeDiagnostics(
                CoreDiagnosticProvider {
                    CoreDiagnosticSnapshot(
                        runtimeState = runtimeState,
                        moduleStates = moduleManager?.getModuleStates()
                              ?: lastModuleStates,
                        failureReason = lastFailureReason
                    )
                }
            )
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
        return diagnosticService.snapshot()
    }

    fun registerRuntimeService(service: RuntimeService) {
        runtimeServiceBootstrap.register(service)
    }

    internal fun setRuntimeServiceProvider(
        provider: RuntimeServiceProvider
    ) {
        runtimeServiceProvider = provider
        runtimeServiceBootstrap = RuntimeServiceBootstrap(
            runtimeServiceProvider,
            RuntimeServiceRegistry()
        )
    }

    internal fun resetRuntimeServiceProvider() {
        runtimeServiceProvider = CoreRuntimeServiceProvider()

        runtimeServiceBootstrap = RuntimeServiceBootstrap(
            runtimeServiceProvider,
            RuntimeServiceRegistry()
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

    private fun installModuleEventBridge() {

        if (moduleEventBridgeInstalled) {
            return
        }

        ModuleEventBus.subscribe { event ->

            if (event is ModuleEvent.Failed) {

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

        val manager = ModuleManager(
            registry = ModuleRegistry(),
            provider = moduleProvider
        )

        installModuleEventBridge()

        try {
            moduleManager = manager

            manager.loadModules()
            manager.startModules()

            runtimeServiceBootstrap.start()

            runtimeState = CoreRuntimeState.RUNNING

            diagnosticEventBus.publish(
                CoreDiagnosticEvent(
                    type = CoreDiagnosticEventType.RUNTIME_STARTED,
                    snapshot = snapshot()
                )
            )

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
        moduleProvider = provider
    }

    internal fun resetModuleProvider() {
        moduleProvider = CoreModuleProvider()
    }

    fun stop() {
        if (runtimeState == CoreRuntimeState.STOPPED) {
            return
        }

        runtimeServiceBootstrap.stop()

        moduleManager?.stopModules()

        moduleManager = null
          runtimeState = CoreRuntimeState.STOPPED
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

        logger.info(
            LogConfig.SYSTEM_STOP,
            "Core runtime stopped"
        )
    }
}
