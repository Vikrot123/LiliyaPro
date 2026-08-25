package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.composition.DefaultCognitiveContextComposition

class CognitiveContextSourceKeyConflictContractTest {

    @Test
    fun later_source_should_not_silently_corrupt_runtime_state() {
        val composition = DefaultCognitiveContextComposition()

        val conflictingSource = source(
            "runtimeState",
            "CORRUPTED"
        )

        composition.service().registerSource(conflictingSource)

        val snapshot =
            composition.service()
                .snapshot(CognitiveContextType.WORKING)

        assertEquals(
            CoreRuntimeState.STOPPED.name,
            snapshot.values["runtimeState"]
        )
    }

    @Test
    fun source_specific_values_should_remain_available() {
        val composition = DefaultCognitiveContextComposition()

        val first = source(
            "shared",
            "first"
        )

        val second = source(
            "shared",
            "second"
        )

        composition.service().registerSource(first)
        composition.service().registerSource(second)

        val snapshot =
            composition.service()
                .snapshot(CognitiveContextType.TASK)

        assertEquals(
            "first",
            snapshot.values["shared"]
        )
    }

    private fun source(
        key: String,
        value: Any?
    ): CognitiveContextSource {
        return object : CognitiveContextSource {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot {
                return CognitiveContextSnapshot(
                    type = type,
                    values = mapOf(key to value)
                )
            }
        }
    }
}
