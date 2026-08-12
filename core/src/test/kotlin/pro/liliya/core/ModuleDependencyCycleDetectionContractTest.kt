package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pro.liliya.core.module.*

class ModuleDependencyCycleDetectionContractTest {

    @Test
    fun dependencyCycleMustAbortResolution() {
        val registry = ModuleRegistry()

        val moduleA = TestModule(
            moduleName = "A",
            dependencies = listOf("B")
        )

        val moduleB = TestModule(
            moduleName = "B",
            dependencies = listOf("A")
        )

        registry.register(moduleA)
        registry.register(moduleB)

        val error = assertThrows(IllegalStateException::class.java) {
            registry.initAll()
        }

        assertTrue(
            error.message?.contains("Dependency cycle") == true
        )
    }

    private class TestModule(
        private val moduleName: String,
        dependencies: List<String>
    ) : LiliyaModule {

        override val descriptor = ModuleDescriptor(
            name = moduleName,
            version = "1.0",
            critical = false,
            dependencies = dependencies
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
