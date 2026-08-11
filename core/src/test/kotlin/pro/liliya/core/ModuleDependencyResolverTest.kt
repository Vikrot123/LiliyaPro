package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import pro.liliya.core.module.LiliyaModule
import pro.liliya.core.module.ModuleDependencyResolver
import pro.liliya.core.module.ModuleDescriptor
import pro.liliya.core.module.ModuleState

class ModuleDependencyResolverTest {

    @Test
    fun resolvesDependencyOrder() {

        val resolver = ModuleDependencyResolver()

        val core = TestModule(
            "CORE_MODULE",
            emptyList()
        )

        val memory = TestModule(
            "MEMORY_MODULE",
            listOf("CORE_MODULE")
        )

        val result = resolver.resolve(
            listOf(memory, core)
        )

        assertEquals(
            listOf(
                "CORE_MODULE",
                "MEMORY_MODULE"
            ),
            result.map { it.name }
        )
    }


    @Test
    fun failsOnMissingDependency() {

        val resolver = ModuleDependencyResolver()

        val module = TestModule(
            "MEMORY_MODULE",
            listOf("UNKNOWN")
        )

        assertThrows(
            IllegalStateException::class.java
        ) {
            resolver.resolve(listOf(module))
        }
    }


    @Test
    fun detectsDependencyCycle() {

        val resolver = ModuleDependencyResolver()

        val a = TestModule(
            "A",
            listOf("B")
        )

        val b = TestModule(
            "B",
            listOf("A")
        )

        assertThrows(
            IllegalStateException::class.java
        ) {
            resolver.resolve(
                listOf(a, b)
            )
        }
    }


    private class TestModule(
        override val name: String,
        dependencies: List<String>
    ) : LiliyaModule {

        override val descriptor =
            ModuleDescriptor(
                name = name,
                version = "0.5",
                critical = false,
                dependencies = dependencies
            )

        override var state =
            ModuleState.CREATED

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
