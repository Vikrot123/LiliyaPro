package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.builder.DefaultCognitiveContextBuilder

class CognitiveContextBuilderContractTest {

    @Test
    fun builder_should_create_snapshot_without_sources() {
        val builder = DefaultCognitiveContextBuilder()

        val snapshot = builder.build(
            CognitiveContextType.TASK,
            emptyList()
        )

        assertNotNull(snapshot)
        assertEquals(
            CognitiveContextType.TASK,
            snapshot.type
        )
        assertTrue(snapshot.values.isEmpty())
    }

    @Test
    fun builder_should_collect_available_source_snapshots() {
        val builder = DefaultCognitiveContextBuilder()

        val source = object : CognitiveContextSource {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot {
                return CognitiveContextSnapshot(
                    type = type,
                    values = mapOf("origin" to "test")
                )
            }
        }

        val snapshot = builder.build(
            CognitiveContextType.WORKING,
            listOf(source)
        )

        assertEquals(
            CognitiveContextType.WORKING,
            snapshot.type
        )

        assertEquals(
            1,
            snapshot.values.size
        )

        assertNotNull(snapshot.values["source_0"])
    }

    @Test
    fun builder_should_ignore_unavailable_source_snapshot() {
        val builder = DefaultCognitiveContextBuilder()

        val unavailableSource = object : CognitiveContextSource {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot? {
                return null
            }
        }

        val snapshot = builder.build(
            CognitiveContextType.SESSION,
            listOf(unavailableSource)
        )

        assertTrue(snapshot.values.isEmpty())
    }

    @Test
    fun builder_should_collect_multiple_sources() {
        val builder = DefaultCognitiveContextBuilder()

        fun source(value: String) =
            object : CognitiveContextSource {
                override fun snapshot(
                    type: CognitiveContextType
                ): CognitiveContextSnapshot {
                    return CognitiveContextSnapshot(
                        type = type,
                        values = mapOf("value" to value)
                    )
                }
            }

        val snapshot = builder.build(
            CognitiveContextType.WORKING,
            listOf(
                source("first"),
                source("second")
            )
        )

        assertEquals(
            2,
            snapshot.values.size
        )

        assertNotNull(snapshot.values["source_0"])
        assertNotNull(snapshot.values["source_1"])
    }

    @Test
    fun builder_should_preserve_requested_context_type() {
        val builder = DefaultCognitiveContextBuilder()

        val source = object : CognitiveContextSource {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot {
                return CognitiveContextSnapshot(type)
            }
        }

        val snapshot = builder.build(
            CognitiveContextType.PROCESSOR,
            listOf(source)
        )

        assertEquals(
            CognitiveContextType.PROCESSOR,
            snapshot.type
        )
    }
}
