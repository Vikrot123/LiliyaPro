package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import pro.liliya.core.module.*

class ModuleDependencyMissingContractTest {

    @Test
    fun missingDependencyMustAbortResolution() {
        val registry = ModuleRegistry()

        val child = TestModule(
            moduleName = "CHILD",
            dependencies = listOf("UNKNOWN")
        )

        registry.register(child)

        assertThrows(IllegalStateException::class.java) {
            registry.initAll()
        }
    }

    private class TestModule(
        private val moduleName: String,
        dependencies: List<String>
    ) : LiliyaModule {

        override val descriptor =
            ModuleDescriptor(
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
