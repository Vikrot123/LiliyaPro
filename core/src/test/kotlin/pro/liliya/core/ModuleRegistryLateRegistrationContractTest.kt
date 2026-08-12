package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import pro.liliya.core.module.*

class ModuleRegistryLateRegistrationContractTest {

    @Test
    fun registrationAfterInitializationMustBeRejected() {
        val registry = ModuleRegistry()

        registry.register(TestModule("FIRST"))

        registry.initAll()

        assertThrows(IllegalStateException::class.java) {
            registry.register(TestModule("LATE"))
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
