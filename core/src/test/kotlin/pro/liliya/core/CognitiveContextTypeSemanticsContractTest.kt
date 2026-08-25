package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.builder.DefaultCognitiveContextBuilder
import pro.liliya.core.runtime.intelligence.context.cognitive.pipeline.DefaultCognitiveContextPipeline
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.DefaultCognitiveContextSelector
import pro.liliya.core.runtime.intelligence.context.cognitive.composition.DefaultCognitiveContextComposition

class CognitiveContextTypeSemanticsContractTest {

    @Test
    fun source_should_receive_exact_requested_context_type() {
        val observed = mutableListOf<CognitiveContextType>()

        val source = object : CognitiveContextSource {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot {
                observed += type
                return CognitiveContextSnapshot(type)
            }
        }

        val pipeline = DefaultCognitiveContextPipeline(
            builder = DefaultCognitiveContextBuilder(),
            selector = DefaultCognitiveContextSelector()
        )

        pipeline.process(
            type = CognitiveContextType.GLOBAL,
            sources = listOf(source)
        )

        pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(source)
        )

        pipeline.process(
            type = CognitiveContextType.TEMPORARY,
            sources = listOf(source)
        )

        assertEquals(
            listOf(
                CognitiveContextType.GLOBAL,
                CognitiveContextType.TASK,
                CognitiveContextType.TEMPORARY
            ),
            observed
        )
    }

    @Test
    fun builder_should_preserve_requested_type_for_every_source_snapshot() {
        val types = CognitiveContextType.values()

        types.forEach { requestedType ->
            val source = object : CognitiveContextSource {
                override fun snapshot(
                    type: CognitiveContextType
                ): CognitiveContextSnapshot {
                    return CognitiveContextSnapshot(
                        type = type,
                        values = mapOf("type" to type.name)
                    )
                }
            }

            val snapshot = DefaultCognitiveContextBuilder().build(
                type = requestedType,
                sources = listOf(source)
            )

            assertEquals(requestedType, snapshot.type)

            val sourceSnapshot =
                snapshot.values["source_0"] as CognitiveContextSnapshot

            assertEquals(requestedType, sourceSnapshot.type)
        }
    }

    @Test
    fun composition_should_create_independent_snapshots_for_different_types() {
        val composition = DefaultCognitiveContextComposition()

        val global = composition
            .service()
            .snapshot(CognitiveContextType.GLOBAL)

        val task = composition
            .service()
            .snapshot(CognitiveContextType.TASK)

        assertEquals(
            CognitiveContextType.GLOBAL,
            global.type
        )

        assertEquals(
            CognitiveContextType.TASK,
            task.type
        )

        assertNotSame(global, task)
    }

    @Test
    fun composition_should_preserve_type_across_all_context_levels() {
        val composition = DefaultCognitiveContextComposition()

        CognitiveContextType.values().forEach { type ->
            val snapshot = composition
                .service()
                .snapshot(type)

            assertEquals(type, snapshot.type)
        }
    }

    @Test
    fun repeated_snapshot_requests_should_not_reuse_previous_type() {
        val composition = DefaultCognitiveContextComposition()

        val first = composition
            .service()
            .snapshot(CognitiveContextType.SESSION)

        val second = composition
            .service()
            .snapshot(CognitiveContextType.PROCESSOR)

        assertEquals(
            CognitiveContextType.SESSION,
            first.type
        )

        assertEquals(
            CognitiveContextType.PROCESSOR,
            second.type
        )

        assertNotSame(first, second)
    }
}
