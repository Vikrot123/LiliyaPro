package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import pro.liliya.core.module.*

class ModuleRegistryDuplicateRegistrationStateIsolationContractTest {

    @Test
    fun duplicateRegistrationMustNotCorruptRegistryState() {
        val registry = ModuleRegistry()

        registry.register(TestModule("DUPLICATE"))

        assertThrows(IllegalStateException::class.java) {
            registry.register(TestModule("DUPLICATE"))
        }

        val states = registry.getStates()

        assertEquals(
            setOf("DUPLICATE"),
            states.keys
        )

        assertEquals(
            1,
            states.size
        )

        assertEquals(
            ModuleState.CREATED,
            states["DUPLICATE"]
        )
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
