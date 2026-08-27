package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionDecisionExplainabilityLifecycleContractTest {

    @Test
    fun explanation_history_is_isolated_between_compositions() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        recordOne(first)

        assertEquals(
            1,
            first
                .decisionExplanationHistory()
                .records()
                .size
        )

        assertEquals(
            0,
            second
                .decisionExplanationHistory()
                .records()
                .size
        )
    }

    @Test
    fun prepare_runtime_clears_explanation_history() {
        val composition =
            DefaultRuntimeComposition()

        recordOne(composition)

        assertEquals(
            1,
            composition
                .decisionExplanationHistory()
                .records()
                .size
        )

        composition.prepareRuntime()

        assertEquals(
            0,
            composition
                .decisionExplanationHistory()
                .records()
                .size
        )
    }

    @Test
    fun explanation_history_can_be_reused_after_prepare_reset() {
        val composition =
            DefaultRuntimeComposition()

        val firstRecord =
            recordOne(composition)

        composition.prepareRuntime()

        val secondRecord =
            recordOne(composition)

        val records =
            composition
                .decisionExplanationHistory()
                .records()

        assertEquals(
            1,
            records.size
        )

        assertEquals(
            secondRecord,
            records.single()
        )

        assertNotEquals(
            firstRecord,
            secondRecord
        )
    }

    private fun recordOne(
        composition: DefaultRuntimeComposition
    ) =
        composition
            .decisionExplanationRecorder()
            .explainAndRecord(
                composition
                    .decisionEngine()
                    .decide(
                        composition
                            .intelligenceOrchestrator()
                            .process()
                    )
            )
}
