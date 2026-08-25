package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

import pro.liliya.core.runtime.intelligence.context.RuntimeContext
import pro.liliya.core.runtime.intelligence.context.RuntimeContextProvider
import pro.liliya.core.runtime.intelligence.context.RuntimeContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.source.DefaultRuntimeCognitiveContextSource

class RuntimeCognitiveContextSourceContractTest {

    private fun createProvider(): RuntimeContextProvider {
        val runtimeSnapshot = RuntimeContextSnapshot(
            runtimeState = "READY",
            activeServices = listOf(
                "service-a",
                "service-b"
            ),
            timestamp = 12345L
        )

        val context = object : RuntimeContext {
            override fun snapshot(): RuntimeContextSnapshot {
                return runtimeSnapshot
            }
        }

        return object : RuntimeContextProvider {
            override fun currentContext(): RuntimeContext {
                return context
            }
        }
    }

    @Test
    fun source_should_provide_runtime_snapshot() {
        val source = DefaultRuntimeCognitiveContextSource(
            createProvider()
        )

        val snapshot = source.snapshot(
            CognitiveContextType.TASK
        )

        assertNotNull(snapshot)
        assertEquals(
            CognitiveContextType.TASK,
            snapshot.type
        )
    }

    @Test
    fun source_should_expose_runtime_state() {
        val source = DefaultRuntimeCognitiveContextSource(
            createProvider()
        )

        val snapshot = source.snapshot(
            CognitiveContextType.WORKING
        )

        assertEquals(
            "READY",
            snapshot.values["runtimeState"]
        )
    }

    @Test
    fun source_should_expose_active_services() {
        val source = DefaultRuntimeCognitiveContextSource(
            createProvider()
        )

        val snapshot = source.snapshot(
            CognitiveContextType.SESSION
        )

        assertEquals(
            listOf("service-a", "service-b"),
            snapshot.values["activeServices"]
        )
    }

    @Test
    fun source_should_preserve_runtime_timestamp() {
        val source = DefaultRuntimeCognitiveContextSource(
            createProvider()
        )

        val snapshot = source.snapshot(
            CognitiveContextType.GLOBAL
        )

        assertEquals(
            12345L,
            snapshot.values["runtimeTimestamp"]
        )
    }

    @Test
    fun source_should_not_share_cognitive_snapshot_state() {
        val source = DefaultRuntimeCognitiveContextSource(
            createProvider()
        )

        val first = source.snapshot(
            CognitiveContextType.TASK
        )

        val second = source.snapshot(
            CognitiveContextType.TASK
        )

        assertNotNull(first)
        assertNotNull(second)

        assertEquals(
            first.type,
            second.type
        )

        assertEquals(
            first.values,
            second.values
        )

        assertTrue(
            first !== second
        )
    }
}
