package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.reflection.RuntimeReflectionSnapshot
import pro.liliya.core.runtime.intelligence.reflection.trend.DefaultRuntimeReflectionTrendAnalyzer

class RuntimeReflectionTrendTransitionContractTest {

    private val analyzer = DefaultRuntimeReflectionTrendAnalyzer()

    private fun snapshot(healthy: Boolean, time: Long) =
        RuntimeReflectionSnapshot(
            summary = if (healthy) "healthy" else "unhealthy",
            healthy = healthy,
            analyzedAt = time
        )

    @Test
    fun unhealthy_to_healthy_should_be_improving() {
        val result = analyzer.analyze(
            listOf(
                snapshot(false, 1L),
                snapshot(true, 2L)
            )
        )

        assertTrue(result.improving)
    }

    @Test
    fun healthy_to_healthy_should_not_be_improving() {
        val result = analyzer.analyze(
            listOf(
                snapshot(true, 1L),
                snapshot(true, 2L)
            )
        )

        assertFalse(result.improving)
    }

    @Test
    fun healthy_to_unhealthy_should_not_be_improving() {
        val result = analyzer.analyze(
            listOf(
                snapshot(true, 1L),
                snapshot(false, 2L)
            )
        )

        assertFalse(result.improving)
    }

    @Test
    fun unhealthy_to_unhealthy_should_not_be_improving() {
        val result = analyzer.analyze(
            listOf(
                snapshot(false, 1L),
                snapshot(false, 2L)
            )
        )

        assertFalse(result.improving)
    }
}
