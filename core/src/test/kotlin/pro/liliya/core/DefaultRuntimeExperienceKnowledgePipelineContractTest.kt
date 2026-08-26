package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
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
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.pipeline.RuntimeKnowledgePipeline
import pro.liliya.core.runtime.intelligence.knowledge.pipeline.RuntimeKnowledgePipelineResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.selfmodel.RuntimeSelfModel

class DefaultRuntimeExperienceKnowledgePipelineContractTest {

    private val context = RuntimeExperienceContext(
        selfModel = RuntimeSelfModel(
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
        ),
        meaning = RuntimeMeaningResult(
            interpretation = "pipeline meaning",
            confidence = 1.0,
            significance = RuntimeMeaningSignificance.WARNING,
            generatedAt = 1L
        )
    )

    private val experience = RuntimeExperience(
        description = "pipeline experience",
        meaning = context.meaning,
        importance = RuntimeExperienceImportance.MEDIUM,
        createdAt = 1L
    )

    private class FakeExperiencePipeline(
        private val result: RuntimeExperiencePipelineResult
    ) : RuntimeExperiencePipeline {

        var calls = 0

        override fun process(
            context: RuntimeExperienceContext
        ): RuntimeExperiencePipelineResult {
            calls++
            return result
        }
    }

    private class FakeConsolidator : RuntimeExperienceConsolidator {

        var calls = 0
        var received: List<RuntimeExperience>? = null

        override fun consolidate(
            experiences: List<RuntimeExperience>
        ): RuntimeExperienceConsolidation {
            calls++
            received = experiences

            return RuntimeExperienceConsolidation(
                summary = "Consolidated ${experiences.size} runtime experiences",
                processedCount = experiences.size,
                createdAt = 2L
            )
        }
    }

    private class FakeKnowledgePipeline(
        private val result: RuntimeKnowledgePipelineResult
    ) : RuntimeKnowledgePipeline {

        var calls = 0
        var received: RuntimeExperienceConsolidation? = null

        override fun process(
            consolidation: RuntimeExperienceConsolidation
        ): RuntimeKnowledgePipelineResult {
            calls++
            received = consolidation
            return result
        }
    }

    private fun knowledge(): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = "Learned runtime knowledge",
            confidence = 0.8,
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = 3L
        )
    }

    @Test
    fun approved_experience_flows_through_consolidation_into_knowledge() {
        val experienceResult = RuntimeExperiencePipelineResult(
            experience = experience,
            decision = RuntimeExperienceDecision(
                shouldRemember = true,
                reason = "Experience may be useful later"
            )
        )

        val experiencePipeline = FakeExperiencePipeline(experienceResult)
        val consolidator = FakeConsolidator()

        val knowledgeResult = RuntimeKnowledgePipelineResult(
            knowledge = knowledge()
        )

        val knowledgePipeline = FakeKnowledgePipeline(knowledgeResult)

        val pipeline = DefaultRuntimeExperienceKnowledgePipeline(
            experiencePipeline = experiencePipeline,
            experienceConsolidator = consolidator,
            knowledgePipeline = knowledgePipeline
        )

        val result = pipeline.process(context)

        assertEquals(1, experiencePipeline.calls)
        assertEquals(1, consolidator.calls)
        assertEquals(1, knowledgePipeline.calls)

        assertSame(experienceResult, result.experienceResult)

        assertEquals(
            listOf(experience),
            consolidator.received
        )

        assertSame(
            result.consolidation,
            knowledgePipeline.received
        )

        assertSame(
            knowledgeResult,
            result.knowledgeResult
        )
    }

    @Test
    fun rejected_experience_stops_before_consolidation_and_knowledge() {
        val experienceResult = RuntimeExperiencePipelineResult(
            experience = experience,
            decision = RuntimeExperienceDecision(
                shouldRemember = false,
                reason = "Experience has low importance"
            )
        )

        val experiencePipeline = FakeExperiencePipeline(experienceResult)
        val consolidator = FakeConsolidator()

        val knowledgePipeline = FakeKnowledgePipeline(
            RuntimeKnowledgePipelineResult(
                knowledge = knowledge()
            )
        )

        val pipeline = DefaultRuntimeExperienceKnowledgePipeline(
            experiencePipeline = experiencePipeline,
            experienceConsolidator = consolidator,
            knowledgePipeline = knowledgePipeline
        )

        val result = pipeline.process(context)

        assertEquals(1, experiencePipeline.calls)
        assertEquals(0, consolidator.calls)
        assertEquals(0, knowledgePipeline.calls)

        assertSame(experienceResult, result.experienceResult)
        assertNull(result.consolidation)
        assertNull(result.knowledgeResult)
    }

    @Test
    fun knowledge_pipeline_receives_exact_consolidation_result() {
        val experienceResult = RuntimeExperiencePipelineResult(
            experience = experience,
            decision = RuntimeExperienceDecision(
                shouldRemember = true,
                reason = "Experience may be useful later"
            )
        )

        val experiencePipeline = FakeExperiencePipeline(experienceResult)
        val consolidator = FakeConsolidator()

        val knowledgePipeline = FakeKnowledgePipeline(
            RuntimeKnowledgePipelineResult(
                knowledge = knowledge()
            )
        )

        val pipeline = DefaultRuntimeExperienceKnowledgePipeline(
            experiencePipeline = experiencePipeline,
            experienceConsolidator = consolidator,
            knowledgePipeline = knowledgePipeline
        )

        val result = pipeline.process(context)

        assertSame(
            result.consolidation,
            knowledgePipeline.received
        )
    }
}
