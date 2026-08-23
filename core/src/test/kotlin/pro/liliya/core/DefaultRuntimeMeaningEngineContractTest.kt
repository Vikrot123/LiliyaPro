package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.context.RuntimeContextMetadata
import pro.liliya.core.runtime.intelligence.context.RuntimeContextSnapshot
import pro.liliya.core.runtime.intelligence.meaning.DefaultRuntimeMeaningEngine
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningContext
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.reflection.RuntimeReflectionSnapshot
import pro.liliya.core.runtime.intelligence.reflection.trend.RuntimeReflectionTrend
import pro.liliya.core.runtime.intelligence.selfmodel.RuntimeSelfModel

class DefaultRuntimeMeaningEngineContractTest {

    @Test
    fun stable_runtime_generates_stable_meaning() {

        val model = RuntimeSelfModel(
            snapshot = RuntimeContextSnapshot(
                runtimeState = "RUNNING",
                activeServices = listOf("runtime"),
                timestamp = 1L
            ),
            metadata = RuntimeContextMetadata(
                runtimeVersion = "1",
                recoveryAvailable = true,
                diagnosticsAvailable = true
            )
        )

        val result =
            DefaultRuntimeMeaningEngine()
                .interpret(
                    RuntimeMeaningContext(
                        selfModel = model,
                        reflection = RuntimeReflectionSnapshot(
                            summary = "healthy",
                            healthy = true,
                            analyzedAt = 1L
                        ),
                        trend = RuntimeReflectionTrend(
                            stability = "STABLE",
                            healthyRatio = 1.0,
                            improving = false
                        )
                    )
                )

        assertEquals(
            RuntimeMeaningSignificance.STABLE,
            result.significance
        )
    }
}
