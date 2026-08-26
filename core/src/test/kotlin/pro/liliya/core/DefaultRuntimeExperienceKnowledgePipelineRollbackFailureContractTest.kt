package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import pro.liliya.core.runtime.intelligence.context.RuntimeContextMetadata
import pro.liliya.core.runtime.intelligence.context.RuntimeContextSnapshot
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperience
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceContext
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceImportance
import pro.liliya.core.runtime.intelligence.experience.consolidation.RuntimeExperienceConsolidation
import pro.liliya.core.runtime.intelligence.experience.consolidation.RuntimeExperienceConsolidator
import pro.liliya.core.runtime.intelligence.experience.decision.RuntimeExperienceDecision
import pro.liliya.core.runtime.intelligence.experience.knowledge.DefaultRuntimeExperienceKnowledgePipeline
import pro.liliya.core.runtime.intelligence.experience.pipeline.RuntimeExperiencePipeline
import pro.liliya.core.runtime.intelligence.experience.pipeline.RuntimeExperiencePipelineResult
import pro.liliya.core.runtime.intelligence.experience.store.RuntimeExperienceStore
import pro.liliya.core.runtime.intelligence.knowledge.pipeline.RuntimeKnowledgePipeline
import pro.liliya.core.runtime.intelligence.knowledge.pipeline.RuntimeKnowledgePipelineResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.selfmodel.RuntimeSelfModel

class DefaultRuntimeExperienceKnowledgePipelineRollbackFailureContractTest {

    @Test
    fun rollback_failure_must_not_replace_original_knowledge_failure() {
        val experience =
            RuntimeExperience(
                description = "rollback failure experience",
                meaning =
                    RuntimeMeaningResult(
                        interpretation = "rollback failure meaning",
                        confidence = 1.0,
                        significance =
                            RuntimeMeaningSignificance.STABLE,
                        generatedAt = 2L
                    ),
                importance =
                    RuntimeExperienceImportance.HIGH,
                createdAt = 3L
            )

        val originalFailure =
            IllegalStateException(
                "original knowledge failure"
            )

        val rollbackFailure =
            IllegalArgumentException(
                "rollback remove failure"
            )

        val experiencePipeline =
            object : RuntimeExperiencePipeline {
                override fun process(
                    context: RuntimeExperienceContext
                ): RuntimeExperiencePipelineResult {
                    return RuntimeExperiencePipelineResult(
                        experience = experience,
                        decision =
                            RuntimeExperienceDecision(
                                shouldRemember = true,
                                reason = "remember"
                            )
                    )
                }
            }

        val store =
            object : RuntimeExperienceStore {

                override fun append(
                    experience: RuntimeExperience
                ) {
                }

                override fun remove(
                    experience: RuntimeExperience
                ): Boolean {
                    throw rollbackFailure
                }

                override fun experiences():
                    List<RuntimeExperience> {
                    return listOf(experience)
                }
            }

        val pipeline =
            DefaultRuntimeExperienceKnowledgePipeline(
                experiencePipeline = experiencePipeline,
                experienceConsolidator =
                    object : RuntimeExperienceConsolidator {
                        override fun consolidate(
                            experiences: List<RuntimeExperience>
                        ): RuntimeExperienceConsolidation {
                            return RuntimeExperienceConsolidation(
                                summary = "one experience",
                                processedCount = 1,
                                createdAt = 4L
                            )
                        }
                    },
                knowledgePipeline =
                    object : RuntimeKnowledgePipeline {
                        override fun process(
                            consolidation:
                                RuntimeExperienceConsolidation
                        ): RuntimeKnowledgePipelineResult {
                            throw originalFailure
                        }
                    },
                experienceStore = store
            )

        val thrown =
            assertFailsWith<IllegalStateException> {
                pipeline.process(context())
            }

        assertSame(
            originalFailure,
            thrown,
            "original knowledge failure must remain primary"
        )

        assertEquals(
            1,
            thrown.suppressed.size,
            "rollback failure must remain observable"
        )

        assertSame(
            rollbackFailure,
            thrown.suppressed.single(),
            "rollback failure must be attached as suppressed"
        )
    }

    private fun context(): RuntimeExperienceContext {
        return RuntimeExperienceContext(
            selfModel =
                RuntimeSelfModel(
                    snapshot =
                        RuntimeContextSnapshot(
                            runtimeState = "RUNNING",
                            activeServices =
                                listOf("runtime"),
                            timestamp = 1L
                        ),
                    metadata =
                        RuntimeContextMetadata(
                            runtimeVersion = "1",
                            recoveryAvailable = true,
                            diagnosticsAvailable = true
                        )
                ),
            meaning =
                RuntimeMeaningResult(
                    interpretation = "context meaning",
                    confidence = 1.0,
                    significance =
                        RuntimeMeaningSignificance.STABLE,
                    generatedAt = 1L
                )
        )
    }
}
