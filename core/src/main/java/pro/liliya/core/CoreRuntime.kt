package pro.liliya.core

import pro.liliya.core.logging.LogConfig
import pro.liliya.core.logging.Logger
import pro.liliya.core.logging.LoggerFactory
import pro.liliya.core.module.CoreModuleProvider
import pro.liliya.core.module.ModuleManager
import pro.liliya.core.module.ModuleRegistry

object CoreRuntime {

    private val context = CoreRuntimeContext()


    private var moduleManager: ModuleManager? = null

    private var moduleProvider: pro.liliya.core.module.ModuleProvider =
        CoreModuleProvider()

    private var runtimeState = CoreRuntimeState.STOPPED

    private var lastFailureReason: String? = null

    private val diagnosticEventBus = context.diagnosticEventBus

    private val diagnosticService =
        CoreRuntimeDiagnosticService(
            CoreRuntimeDiagnostics(
                CoreDiagnosticProvider {
                    CoreDiagnosticSnapshot(
                        runtimeState = runtimeState,
                        moduleStates = moduleManager?.getModuleStates()
                            ?: emptyMap(),
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

        try {
            moduleManager = manager

            manager.loadModules()
            manager.startModules()

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

        moduleManager?.stopModules()

        moduleManager = null
        runtimeState = CoreRuntimeState.STOPPED
        lastFailureReason = null

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
