package pro.liliya.core

import org.junit.jupiter.api.Test
import java.io.File
import pro.liliya.core.logging.LogInitializer
import pro.liliya.core.module.LiliyaModule
import pro.liliya.core.module.ModuleDescriptor
import pro.liliya.core.module.ModuleManager
import pro.liliya.core.module.ModuleProvider
import pro.liliya.core.module.ModuleRegistry
import pro.liliya.core.module.ModuleState

class ModuleCriticalFailureReadyTest {

    @Test
    fun criticalFailureMustNotReportModulesStarted() {

        val logFile = File(
            "build/liliya-critical-failure-ready.log"
        )

        logFile.delete()

        LogInitializer.initialize(logFile)

        val registry = ModuleRegistry()

        val provider = object : ModuleProvider {

            override fun provideModules(): List<LiliyaModule> {
                return listOf(
                    CriticalFailingModule()
                )
            }
        }

        val manager = ModuleManager(
            registry,
            provider
        )

        manager.loadModules()

        try {
            manager.startModules()

            throw AssertionError(
                "Critical module failure must abort startup"
            )
        } catch (e: IllegalStateException) {
            // Expected.
        }

        val text = logFile.readText()

        require(
            !text.contains("Modules started")
        ) {
            "Critical failure must not report Modules started"
        }

        require(
            !text.contains("Core runtime ready")
        ) {
            "Critical failure must not report Core runtime ready"
        }

        require(
            registry.getStates()["CRITICAL_FAILING_MODULE"] ==
                ModuleState.FAILED
        ) {
            "Critical module must remain FAILED"
        }
    }

    private class CriticalFailingModule : LiliyaModule {

        override val descriptor = ModuleDescriptor(
            name = "CRITICAL_FAILING_MODULE",
            version = "1.0",
            critical = true
        )

        override var state = ModuleState.CREATED

        override fun init() {
            state = ModuleState.INITIALIZED
        }

        override fun start() {
            state = ModuleState.FAILED

            throw IllegalStateException(
                "Intentional critical failure"
            )
        }

        override fun stop() {
            state = ModuleState.STOPPED
        }
    }
}
