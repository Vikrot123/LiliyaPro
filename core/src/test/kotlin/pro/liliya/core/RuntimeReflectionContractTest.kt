package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.context.RuntimeContextMetadata
import pro.liliya.core.runtime.intelligence.context.RuntimeContextSnapshot
import pro.liliya.core.runtime.intelligence.reflection.RuntimeReflection
import pro.liliya.core.runtime.intelligence.reflection.RuntimeReflectionSnapshot
import pro.liliya.core.runtime.intelligence.selfmodel.RuntimeSelfModel

class RuntimeReflectionContractTest {

    @Test
    fun reflection_analyzes_self_model() {

        val model = RuntimeSelfModel(
            snapshot = RuntimeContextSnapshot(
                runtimeState = "RUNNING",
                activeServices = listOf("runtime"),
                timestamp = 1L
            ),
            metadata = RuntimeContextMetadata(
                runtimeVersion = "1.188",
                recoveryAvailable = true,
                diagnosticsAvailable = true
            )
        )

        val reflection = object : RuntimeReflection {

            override fun analyze(
                selfModel: RuntimeSelfModel
            ): RuntimeReflectionSnapshot {

                return RuntimeReflectionSnapshot(
                    summary = selfModel.snapshot.runtimeState,
                    healthy = true,
                    analyzedAt = 1L
                )
            }
        }

        val result = reflection.analyze(model)

        assertEquals(
            "RUNNING",
            result.summary
        )

        assertEquals(
            true,
            result.healthy
        )
    }
}
