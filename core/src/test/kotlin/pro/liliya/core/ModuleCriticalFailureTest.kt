package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import pro.liliya.core.module.LiliyaModule
import pro.liliya.core.module.ModuleDescriptor
import pro.liliya.core.module.ModuleManager
import pro.liliya.core.module.ModuleRegistry
import pro.liliya.core.module.ModuleState
import pro.liliya.core.module.ModuleProvider

class ModuleCriticalFailureTest {

    @Test
    fun criticalModuleFailureMustAbortModuleStartup() {

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

        assertThrows(
            IllegalStateException::class.java
        ) {
            manager.startModules()
        }

        assertEquals(
            ModuleState.FAILED,
            registry.getStates()["CRITICAL_FAILING_MODULE"]
        )
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
