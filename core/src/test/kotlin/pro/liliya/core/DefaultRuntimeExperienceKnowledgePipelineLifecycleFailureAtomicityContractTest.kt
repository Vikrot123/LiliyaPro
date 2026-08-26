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
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.integration.RuntimeKnowledgeMemory
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.integration.RuntimeKnowledgeLifecycleMemory
import pro.liliya.core.runtime.intelligence.knowledge.pipeline.RuntimeKnowledgePipeline
import pro.liliya.core.runtime.intelligence.knowledge.pipeline.RuntimeKnowledgePipelineResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.selfmodel.RuntimeSelfModel

class DefaultRuntimeExperienceKnowledgePipelineLifecycleFailureAtomicityContractTest {

    @Test
    fun failed_lifecycle_commit_must_rollback_experience_and_preserve_original_failure() {
        val store =
            DefaultRuntimeExperienceStore()

        val experience =
            RuntimeExperience(
                description = "lifecycle failure experience",
                meaning =
                    RuntimeMeaningResult(
                        interpretation = "meaning",
                        confidence = 1.0,
                        significance =
                            RuntimeMeaningSignificance.STABLE,
                        generatedAt = 2L
                    ),
                importance =
                    RuntimeExperienceImportance.HIGH,
                createdAt = 3L
            )

        val knowledge =
            RuntimeKnowledge(
                statement = "derived knowledge",
                confidence = 0.9,
                source =
                    RuntimeKnowledgeSource.CONSOLIDATION,
                createdAt = 4L
            )

        val lifecycleFailure =
            IllegalStateException(
                "forced lifecycle commit failure"
            )

        val experiencePipeline =
            DefaultRuntimeExperiencePipeline(
                experienceEngine =
                    object : RuntimeExperienceEngine {
                        override fun createExperience(
                            context: RuntimeExperienceContext
                        ): RuntimeExperience {
                            return experience
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
                                processedCount = 1,
                                createdAt = 5L
                            )
                        }
                    },
                knowledgePipeline =
                    object : RuntimeKnowledgePipeline {
                        override fun process(
                            consolidation:
                                RuntimeExperienceConsolidation
                        ): RuntimeKnowledgePipelineResult {
                            return RuntimeKnowledgePipelineResult(
                                knowledge = knowledge
                            )
                        }
                    },
                experienceStore = store,
                knowledgeLifecycleMemory =
                    object : RuntimeKnowledgeLifecycleMemory {

                        override fun create(
                            knowledge: RuntimeKnowledge
                        ) {
                            throw lifecycleFailure
                        }

                        override fun activate(
                            knowledge: RuntimeKnowledge
                        ) {
                        }

                        override fun revise(
                            knowledge: RuntimeKnowledge
                        ) {
                        }

                        override fun archive(
                            knowledge: RuntimeKnowledge
                        ) {
                        }

                        override fun memory():
                            RuntimeKnowledgeMemory {
                            error("must not be called")
                        }
                    }
            )

        val thrown =
            assertFailsWith<IllegalStateException> {
                pipeline.process(context())
            }

        assertSame(
            lifecycleFailure,
            thrown,
            "lifecycle commit failure must remain primary"
        )

        assertEquals(
            emptyList(),
            store.experiences(),
            "failed lifecycle commit must rollback the experience append"
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
