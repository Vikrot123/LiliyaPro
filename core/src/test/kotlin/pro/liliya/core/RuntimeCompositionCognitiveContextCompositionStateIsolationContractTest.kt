package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionCognitiveContextCompositionStateIsolationContractTest {

    private fun source(value: String): CognitiveContextSource {
        return object : CognitiveContextSource {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot {
                return CognitiveContextSnapshot(
                    type = type,
                    values = mapOf("value" to value)
                )
            }
        }
    }

    @Test
    fun cognitive_source_registered_through_runtime_composition_should_remain_visible() {
        val composition = DefaultRuntimeComposition()
        val cognitive = composition.cognitiveContextComposition()
        val source = source("runtime")

        assertTrue(
            cognitive.service().registerSource(source)
        )

        val sameCognitive = composition.cognitiveContextComposition()

        assertEquals(
            2,
            sameCognitive.sourceRegistry().size()
        )

        val result = sameCognitive.service().process(
            CognitiveContextType.TASK
        )

        assertTrue(
            result.values.values.any {
                it is CognitiveContextSnapshot &&
                    it.values["value"] == "runtime"
            }
        )
    }

    @Test
    fun cognitive_lifecycle_state_should_remain_visible_through_runtime_composition() {
        val composition = DefaultRuntimeComposition()

        val cognitive = composition.cognitiveContextComposition()

        assertFalse(cognitive.lifecycle().isStarted())

        cognitive.lifecycle().start()

        assertTrue(
            composition.cognitiveContextComposition()
                .lifecycle()
                .isStarted()
        )

        composition.cognitiveContextComposition()
            .lifecycle()
            .reset()

        assertFalse(
            cognitive.lifecycle().isStarted()
        )
    }

    @Test
    fun separate_runtime_compositions_should_isolate_cognitive_source_state() {
        val first = DefaultRuntimeComposition()
        val second = DefaultRuntimeComposition()

        val firstCognitive = first.cognitiveContextComposition()
        val secondCognitive = second.cognitiveContextComposition()

        val firstSource = source("first")

        assertTrue(
            firstCognitive.service().registerSource(firstSource)
        )

        assertEquals(
            2,
            firstCognitive.sourceRegistry().size()
        )

        assertEquals(
            1,
            secondCognitive.sourceRegistry().size()
        )

        val secondResult = secondCognitive.service().process(
            CognitiveContextType.TASK
        )

        assertTrue(
            secondResult.values.values.none {
                it is CognitiveContextSnapshot &&
                    it.values["value"] == "first"
            }
        )
    }

    @Test
    fun separate_runtime_compositions_should_isolate_cognitive_lifecycle_state() {
        val first = DefaultRuntimeComposition()
        val second = DefaultRuntimeComposition()

        first.cognitiveContextComposition()
            .lifecycle()
            .start()

        assertTrue(
            first.cognitiveContextComposition()
                .lifecycle()
                .isStarted()
        )

        assertFalse(
            second.cognitiveContextComposition()
                .lifecycle()
                .isStarted()
        )
    }
}
