package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import pro.liliya.core.runtime.intelligence.context.RuntimeContextMetadata
import pro.liliya.core.runtime.intelligence.context.RuntimeContextSnapshot
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperience
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceContext
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceEngine
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceImportance
import pro.liliya.core.runtime.intelligence.experience.consolidation.RuntimeExperienceConsolidation
import pro.liliya.core.runtime.intelligence.experience.consolidation.RuntimeExperienceConsolidator
import pro.liliya.core.runtime.intelligence.experience.decision.RuntimeExperienceDecision
import pro.liliya.core.runtime.intelligence.experience.decision.RuntimeExperienceDecisionEngine
import pro.liliya.core.runtime.intelligence.experience.knowledge.DefaultRuntimeExperienceKnowledgePipeline
import pro.liliya.core.runtime.intelligence.experience.pipeline.DefaultRuntimeExperiencePipeline
import pro.liliya.core.runtime.intelligence.experience.store.DefaultRuntimeExperienceStore
import pro.liliya.core.runtime.intelligence.knowledge.pipeline.RuntimeKnowledgePipeline
import pro.liliya.core.runtime.intelligence.knowledge.pipeline.RuntimeKnowledgePipelineResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.selfmodel.RuntimeSelfModel

class DefaultRuntimeExperienceKnowledgePipelineRollbackOwnershipContractTest {

    @Test
    fun failed_knowledge_stage_must_preserve_preexisting_equal_experience() {
        val store =
            DefaultRuntimeExperienceStore()

        val preexisting =
            experience()

        val current =
            experience()

        store.append(preexisting)

        val experiencePipeline =
            DefaultRuntimeExperiencePipeline(
                experienceEngine =
                    object : RuntimeExperienceEngine {
                        override fun createExperience(
                            context: RuntimeExperienceContext
                        ): RuntimeExperience {
                            return current
                        }
                    },
                decisionEngine =
                    object : RuntimeExperienceDecisionEngine {
                        override fun decide(
                            experience: RuntimeExperience
                        ): RuntimeExperienceDecision {
                            return RuntimeExperienceDecision(
                                shouldRemember = true,
                                reason = "remember"
                            )
                        }
                    },
                experienceStore = store
            )

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
                                processedCount = experiences.size,
                                createdAt = 4L
                            )
                        }
                    },
                knowledgePipeline =
                    object : RuntimeKnowledgePipeline {
                        override fun process(
                            consolidation: RuntimeExperienceConsolidation
                        ): RuntimeKnowledgePipelineResult {
                            error("forced knowledge failure")
                        }
                    },
                experienceStore = store
            )

        assertFailsWith<IllegalStateException> {
            pipeline.process(context())
        }

        val remaining =
            store.experiences()

        assertEquals(
            1,
            remaining.size,
            "rollback must remove only the current orchestration append"
        )

        assertSame(
            preexisting,
            remaining.single(),
            "preexisting equal experience must survive rollback"
        )
    }

    private fun experience(): RuntimeExperience {
        return RuntimeExperience(
            description = "equal experience",
            meaning =
                RuntimeMeaningResult(
                    interpretation = "equal meaning",
                    confidence = 1.0,
                    significance =
                        RuntimeMeaningSignificance.STABLE,
                    generatedAt = 2L
                ),
            importance =
                RuntimeExperienceImportance.HIGH,
            createdAt = 3L
        )
    }

    private fun context(): RuntimeExperienceContext {
        return RuntimeExperienceContext(
            selfModel =
                RuntimeSelfModel(
                    snapshot =
                        RuntimeContextSnapshot(
                            runtimeState = "RUNNING",
                            activeServices = listOf("runtime"),
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
