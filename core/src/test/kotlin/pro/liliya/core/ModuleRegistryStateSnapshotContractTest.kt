package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pro.liliya.core.module.LiliyaModule
import pro.liliya.core.module.ModuleDescriptor
import pro.liliya.core.module.ModuleRegistry
import pro.liliya.core.module.ModuleState

class ModuleRegistryStateSnapshotContractTest {

    private class SnapshotModule : LiliyaModule {

        override val descriptor = ModuleDescriptor(
            name = "SNAPSHOT_MODULE",
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

    @Test
    fun registryExposesCurrentModuleStates() {

        val registry = ModuleRegistry()
        val module = SnapshotModule()

        registry.register(module)

        assertEquals(
            ModuleState.CREATED,
            registry.getStates()["SNAPSHOT_MODULE"]
        )

        registry.initAll()

        assertEquals(
            ModuleState.INITIALIZED,
            registry.getStates()["SNAPSHOT_MODULE"]
        )

        registry.startAll()

        assertEquals(
            ModuleState.RUNNING,
            registry.getStates()["SNAPSHOT_MODULE"]
        )

        registry.stopAll()

        assertEquals(
            ModuleState.STOPPED,
            registry.getStates()["SNAPSHOT_MODULE"]
        )
    }
}
