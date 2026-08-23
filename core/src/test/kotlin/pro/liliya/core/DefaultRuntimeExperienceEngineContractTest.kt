package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.experience.DefaultRuntimeExperienceEngine
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceContext
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceImportance
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.selfmodel.RuntimeSelfModel
import pro.liliya.core.runtime.intelligence.context.RuntimeContextMetadata
import pro.liliya.core.runtime.intelligence.context.RuntimeContextSnapshot

class DefaultRuntimeExperienceEngineContractTest {

    @Test
    fun critical_meaning_creates_high_importance_experience() {

        val selfModel = RuntimeSelfModel(
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

        val meaning = RuntimeMeaningResult(
            interpretation = "Runtime instability requires attention",
            confidence = 0.85,
            significance = RuntimeMeaningSignificance.CRITICAL,
            generatedAt = 1L
        )

        val experience =
            DefaultRuntimeExperienceEngine()
                .createExperience(
                    RuntimeExperienceContext(
                        selfModel = selfModel,
                        meaning = meaning
                    )
                )

        assertEquals(
            RuntimeExperienceImportance.HIGH,
            experience.importance
        )
    }
}
