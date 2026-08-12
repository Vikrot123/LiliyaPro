package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import pro.liliya.core.module.*

class ModuleRegistryStartBeforeInitContractTest {

    @Test
    fun startBeforeInitializationMustBeRejected() {
        val registry = ModuleRegistry()

        registry.register(TestModule("CORE"))

        assertThrows(IllegalStateException::class.java) {
            registry.startAll()
        }
    }

    private class TestModule(
        private val moduleName: String
    ) : LiliyaModule {

        override val descriptor = ModuleDescriptor(
            name = moduleName,
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
