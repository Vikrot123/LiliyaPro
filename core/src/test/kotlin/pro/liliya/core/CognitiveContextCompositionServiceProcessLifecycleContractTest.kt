package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.composition.DefaultCognitiveContextComposition

class CognitiveContextCompositionServiceProcessLifecycleContractTest {

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
    fun repeated_process_should_not_accumulate_previous_results() {
        val composition = DefaultCognitiveContextComposition()
        val source = source("first")

        composition.service().registerSource(source)

        val first = composition.service().process(
            CognitiveContextType.TASK
        )

        val second = composition.service().process(
            CognitiveContextType.TASK
        )

        assertEquals(first.values, second.values)
        assertEquals(first.selectedCount, second.selectedCount)
        assertEquals(first.rejectedCount, second.rejectedCount)
    }

    @Test
    fun unregister_should_affect_following_process_without_affecting_previous_result() {
        val composition = DefaultCognitiveContextComposition()
        val source = source("temporary")

        composition.service().registerSource(source)

        val beforeUnregister = composition.service().process(
            CognitiveContextType.TASK
        )

        assertTrue(
            beforeUnregister.values.values.any {
                it is CognitiveContextSnapshot &&
                    it.values["value"] == "temporary"
            }
        )

        assertTrue(
            composition.service().unregisterSource(source)
        )

        val afterUnregister = composition.service().process(
            CognitiveContextType.TASK
        )

        assertTrue(
            beforeUnregister.values.values.any {
                it is CognitiveContextSnapshot &&
                    it.values["value"] == "temporary"
            }
        )

        assertTrue(
            afterUnregister.values.values.none {
                it is CognitiveContextSnapshot &&
                    it.values["value"] == "temporary"
            }
        )
    }

    @Test
    fun different_context_types_should_not_change_source_registry_state() {
        val composition = DefaultCognitiveContextComposition()
        val source = source("stable")

        composition.service().registerSource(source)

        val initialSize = composition.service()
            .sourceRegistry()
            .size()

        composition.service().process(
            CognitiveContextType.TASK
        )

        composition.service().process(
            CognitiveContextType.WORKING
        )

        composition.service().process(
            CognitiveContextType.SESSION
        )

        assertEquals(
            initialSize,
            composition.service()
                .sourceRegistry()
                .size()
        )
    }
}
