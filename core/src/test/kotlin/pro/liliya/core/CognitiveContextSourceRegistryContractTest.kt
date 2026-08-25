package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotSame
import kotlin.test.assertTrue

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.registry.DefaultCognitiveContextSourceRegistry

class CognitiveContextSourceRegistryContractTest {

    private fun source(
        name: String
    ): CognitiveContextSource {
        return object : CognitiveContextSource {

            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot {
                return CognitiveContextSnapshot(
                    type = type,
                    values = mapOf("name" to name)
                )
            }
        }
    }

    @Test
    fun registry_should_start_empty() {
        val registry = DefaultCognitiveContextSourceRegistry()

        assertEquals(0, registry.size())
        assertTrue(registry.sources().isEmpty())
    }

    @Test
    fun registry_should_register_source() {
        val registry = DefaultCognitiveContextSourceRegistry()
        val source = source("runtime")

        assertTrue(registry.register(source))

        assertEquals(1, registry.size())
        assertEquals(
            listOf(source),
            registry.sources()
        )
    }

    @Test
    fun registry_should_reject_duplicate_source() {
        val registry = DefaultCognitiveContextSourceRegistry()
        val source = source("runtime")

        assertTrue(registry.register(source))
        assertFalse(registry.register(source))

        assertEquals(1, registry.size())
    }

    @Test
    fun registry_should_unregister_source() {
        val registry = DefaultCognitiveContextSourceRegistry()
        val source = source("runtime")

        registry.register(source)

        assertTrue(registry.unregister(source))
        assertFalse(registry.unregister(source))

        assertEquals(0, registry.size())
    }

    @Test
    fun registry_should_preserve_registration_order() {
        val registry = DefaultCognitiveContextSourceRegistry()

        val first = source("first")
        val second = source("second")
        val third = source("third")

        registry.register(first)
        registry.register(second)
        registry.register(third)

        assertEquals(
            listOf(first, second, third),
            registry.sources()
        )
    }

    @Test
    fun registry_should_return_snapshot_of_sources() {
        val registry = DefaultCognitiveContextSourceRegistry()
        val source = source("runtime")

        registry.register(source)

        val first = registry.sources()
        val second = registry.sources()

        assertEquals(first, second)
        assertNotSame(first, second)
    }

    @Test
    fun registry_should_clear_all_sources() {
        val registry = DefaultCognitiveContextSourceRegistry()

        registry.register(source("first"))
        registry.register(source("second"))

        registry.clear()

        assertEquals(0, registry.size())
        assertTrue(registry.sources().isEmpty())
    }

    @Test
    fun separate_registries_should_not_share_sources() {
        val firstRegistry =
            DefaultCognitiveContextSourceRegistry()

        val secondRegistry =
            DefaultCognitiveContextSourceRegistry()

        val source = source("runtime")

        firstRegistry.register(source)

        assertEquals(1, firstRegistry.size())
        assertEquals(0, secondRegistry.size())
    }
}
