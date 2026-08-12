package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import pro.liliya.core.module.*

class ModuleManagerLifecycleContractTest {

    @Test
    fun moduleManagerMustControlRegistryLifecycle() {

        val registry = ModuleRegistry()

        val provider = object : ModuleProvider {
            override fun provideModules(): List<LiliyaModule> {
                return listOf(TestModule())
            }
        }

        val manager = ModuleManager(
            registry,
            provider
        )

        manager.loadModules()

        manager.startModules()

        assertEquals(
            ModuleState.RUNNING,
            manager.getModuleStates()["TEST_MODULE"]
        )

        manager.stopModules()

        assertEquals(
            ModuleState.STOPPED,
            manager.getModuleStates()["TEST_MODULE"]
        )

        assertThrows(IllegalStateException::class.java) {
            manager.startModules()
        }
    }


    private class TestModule : LiliyaModule {

        override val descriptor = ModuleDescriptor(
            name = "TEST_MODULE",
            version = "1.0",
            critical = false,
            dependencies = emptyList()
        )

        override var state = ModuleState.CREATED

        override fun init() {
            state = ModuleState.INITIALIZED
        }

        override fun start() {
            state = ModuleState.RUNNING
        }

        override fun stop() {
            state = ModuleState.STOPPED
        }
    }
}
