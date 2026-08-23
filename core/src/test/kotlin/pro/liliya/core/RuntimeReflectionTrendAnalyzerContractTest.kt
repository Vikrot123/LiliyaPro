package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.reflection.RuntimeReflectionSnapshot
import pro.liliya.core.runtime.intelligence.reflection.trend.DefaultRuntimeReflectionTrendAnalyzer
import pro.liliya.core.runtime.intelligence.reflection.trend.RuntimeReflectionStability

class RuntimeReflectionTrendAnalyzerContractTest {

    @Test
    fun empty_history_returns_unknown_trend() {

        val result =
            DefaultRuntimeReflectionTrendAnalyzer()
                .analyze(emptyList())

        assertEquals(
            RuntimeReflectionStability.UNKNOWN,
            result.stability
        )

        assertEquals(
            0.0,
            result.healthyRatio
        )

        assertFalse(
            result.improving
        )
    }


    @Test
    fun healthy_history_is_stable() {

        val history =
            listOf(
                RuntimeReflectionSnapshot(
                    summary = "one",
                    healthy = true,
                    analyzedAt = 1L
                ),
                RuntimeReflectionSnapshot(
                    summary = "two",
                    healthy = true,
                    analyzedAt = 2L
                ),
                RuntimeReflectionSnapshot(
                    summary = "three",
                    healthy = true,
                    analyzedAt = 3L
                )
            )

        val result =
            DefaultRuntimeReflectionTrendAnalyzer()
                .analyze(history)

        assertEquals(
            RuntimeReflectionStability.STABLE,
            result.stability
        )

        assertEquals(
            1.0,
            result.healthyRatio
        )

        assertTrue(
            result.improving
        )
    }


    @Test
    fun unhealthy_history_becomes_unstable() {

        val history =
            listOf(
                RuntimeReflectionSnapshot(
                    summary = "failed",
                    healthy = false,
                    analyzedAt = 1L
                ),
                RuntimeReflectionSnapshot(
                    summary = "failed",
                    healthy = false,
                    analyzedAt = 2L
                )
            )

        val result =
            DefaultRuntimeReflectionTrendAnalyzer()
                .analyze(history)

        assertEquals(
            RuntimeReflectionStability.UNSTABLE,
            result.stability
        )

        assertEquals(
            0.0,
            result.healthyRatio
        )
    }
}
