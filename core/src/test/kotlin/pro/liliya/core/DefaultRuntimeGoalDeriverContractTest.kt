package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.goal.DefaultRuntimeGoalDeriver
import pro.liliya.core.runtime.intelligence.goal.RuntimeGoalPriority
import pro.liliya.core.runtime.intelligence.goal.RuntimeGoalState
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class DefaultRuntimeGoalDeriverContractTest {

    private val deriver =
        DefaultRuntimeGoalDeriver()

    @Test
    fun stable_meaning_derives_maintenance_goal() {
        val goal =
            deriver.derive(
                RuntimeIntelligenceFixture.result(
                    significance =
                        RuntimeMeaningSignificance.STABLE,
                    confidence = 0.90
                )
            )

        assertEquals(
            RuntimeGoalState.MAINTAIN,
            goal.state
        )

        assertEquals(
            RuntimeGoalPriority.LOW,
            goal.priority
        )

        assertFalse(
            goal.actionable
        )

        assertEquals(
            0.90,
            goal.confidence
        )
    }

    @Test
    fun warning_meaning_derives_investigation_goal() {
        val goal =
            deriver.derive(
                RuntimeIntelligenceFixture.result(
                    significance =
                        RuntimeMeaningSignificance.WARNING,
                    confidence = 0.80
                )
            )

        assertEquals(
            RuntimeGoalState.INVESTIGATE,
            goal.state
        )

        assertEquals(
            RuntimeGoalPriority.HIGH,
            goal.priority
        )

        assertTrue(
            goal.actionable
        )
    }

    @Test
    fun critical_meaning_derives_recovery_goal() {
        val goal =
            deriver.derive(
                RuntimeIntelligenceFixture.result(
                    significance =
                        RuntimeMeaningSignificance.CRITICAL,
                    confidence = 0.95
                )
            )

        assertEquals(
            RuntimeGoalState.RECOVER,
            goal.state
        )

        assertEquals(
            RuntimeGoalPriority.CRITICAL,
            goal.priority
        )

        assertTrue(
            goal.actionable
        )
    }

    @Test
    fun unknown_meaning_derives_uncertainty_reduction_goal() {
        val goal =
            deriver.derive(
                RuntimeIntelligenceFixture.result(
                    significance =
                        RuntimeMeaningSignificance.UNKNOWN,
                    confidence = 0.50
                )
            )

        assertEquals(
            RuntimeGoalState.OBSERVE,
            goal.state
        )

        assertEquals(
            RuntimeGoalPriority.NORMAL,
            goal.priority
        )

        assertTrue(
            goal.actionable
        )
    }
}
