package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
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

class DefaultRuntimeExperienceKnowledgePipelineFailureAtomicityContractTest {

    @Test
    fun failed_knowledge_stage_must_not_leave_partial_experience_commit() {
        val store =
            DefaultRuntimeExperienceStore()

        val experience =
            RuntimeExperience(
                description = "atomic experience candidate",
                meaning =
                    RuntimeMeaningResult(
                        interpretation = "candidate",
                        confidence = 1.0,
                        significance =
                            RuntimeMeaningSignificance.STABLE,
                        generatedAt = 2L
                    ),
                importance =
                    RuntimeExperienceImportance.HIGH,
                createdAt = 3L
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
                                reason = "Experience may be useful later"
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
                                summary =
                                    "Consolidated ${experiences.size} runtime experiences",
                                processedCount =
                                    experiences.size,
                                createdAt = 4L
                            )
                        }
                    },
                experienceStore = store,
                knowledgePipeline =
                    object : RuntimeKnowledgePipeline {
                        override fun process(
                            consolidation:
                                RuntimeExperienceConsolidation
                        ): RuntimeKnowledgePipelineResult {
                            error("forced knowledge failure")
                        }
                    }
            )

        val context =
            RuntimeExperienceContext(
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
                        interpretation = "meaning",
                        confidence = 1.0,
                        significance =
                            RuntimeMeaningSignificance.STABLE,
                        generatedAt = 1L
                    )
            )

        assertFailsWith<IllegalStateException> {
            pipeline.process(context)
        }

        assertEquals(
            emptyList(),
            store.experiences(),
            "failed knowledge stage must not leave a partial experience commit"
        )
    }
}
