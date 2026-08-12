package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import pro.liliya.core.module.LiliyaModule
import pro.liliya.core.module.ModuleDescriptor
import pro.liliya.core.module.ModuleRegistry
import pro.liliya.core.module.ModuleState

class ModuleRegistryStateQueryContractTest {

    private class RunningModule(
        private val moduleName: String
    ) : LiliyaModule {

        override val descriptor = ModuleDescriptor(
            name = moduleName,
            version = "1.0",
            critical = false
        )

        override var state: ModuleState = ModuleState.CREATED

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

    private class FailingModule : LiliyaModule {

        override val descriptor = ModuleDescriptor(
            name = "FAILED_MODULE",
            version = "1.0",
            critical = false
        )

        override var state: ModuleState = ModuleState.CREATED

        override fun init() {
            state = ModuleState.INITIALIZED
        }

        override fun start() {
            throw IllegalStateException("failure")
        }

        override fun stop() {
            state = ModuleState.STOPPED
        }
    }

    @Test
    fun registryStateQueryReturnsStableModuleSnapshot() {

        val registry = ModuleRegistry()

        val first = RunningModule("FIRST")
        val second = RunningModule("SECOND")

        registry.register(first)
        registry.register(second)

        assertNull(
            registry.getStates()["UNKNOWN"]
        )

        registry.initAll()
        registry.startAll()

        val states = registry.getStates()

        assertEquals(
            ModuleState.RUNNING,
            states["FIRST"]
        )

        assertEquals(
            ModuleState.RUNNING,
            states["SECOND"]
        )
    }

    @Test
    fun failedModuleStateRemainsVisibleInSnapshot() {

        val registry = ModuleRegistry()

        registry.register(FailingModule())

        registry.initAll()
        registry.startAll()

        assertEquals(
            ModuleState.FAILED,
            registry.getStates()["FAILED_MODULE"]
        )
    }
}
