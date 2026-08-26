package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

import pro.liliya.core.runtime.intelligence.context.RuntimeContextMetadata
import pro.liliya.core.runtime.intelligence.context.RuntimeContextSnapshot
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperience
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceContext
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceImportance
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceEngine
import pro.liliya.core.runtime.intelligence.experience.decision.RuntimeExperienceDecision
import pro.liliya.core.runtime.intelligence.experience.decision.RuntimeExperienceDecisionEngine
import pro.liliya.core.runtime.intelligence.experience.pipeline.DefaultRuntimeExperiencePipeline
import pro.liliya.core.runtime.intelligence.experience.store.RuntimeExperienceStore
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.selfmodel.RuntimeSelfModel

class DefaultRuntimeExperiencePipelineContractTest {

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

    private class FakeExperienceEngine(
        private val result: RuntimeExperience
    ) : RuntimeExperienceEngine {

        var calls = 0

        override fun createExperience(
            context: RuntimeExperienceContext
        ): RuntimeExperience {
            calls++
            return result
        }
    }

    private class FakeDecisionEngine(
        private val result: RuntimeExperienceDecision
    ) : RuntimeExperienceDecisionEngine {

        var calls = 0
        var received: RuntimeExperience? = null

        override fun decide(
            experience: RuntimeExperience
        ): RuntimeExperienceDecision {
            calls++
            received = experience
            return result
        }
    }

    private class FakeExperienceStore : RuntimeExperienceStore {

        val stored = mutableListOf<RuntimeExperience>()

        override fun append(
            experience: RuntimeExperience
        ) {
            stored += experience
        }

        override fun remove(
            experience: RuntimeExperience
        ): Boolean {
            return stored.remove(experience)
        }

        override fun experiences(): List<RuntimeExperience> {
            return stored.toList()
        }
    }

    @Test
    fun pipeline_creates_decides_and_remembers_when_approved() {
        val engine = FakeExperienceEngine(experience)
        val decisionEngine = FakeDecisionEngine(
            RuntimeExperienceDecision(
                shouldRemember = true,
                reason = "Experience may be useful later"
            )
        )
        val store = FakeExperienceStore()

        val pipeline = DefaultRuntimeExperiencePipeline(
            experienceEngine = engine,
            decisionEngine = decisionEngine,
            experienceStore = store
        )

        val result = pipeline.process(context)

        assertEquals(1, engine.calls)
        assertEquals(1, decisionEngine.calls)
        assertSame(experience, decisionEngine.received)
        assertEquals(listOf(experience), store.stored)
        assertSame(experience, result.experience)
        assertEquals(true, result.decision.shouldRemember)
    }

    @Test
    fun pipeline_does_not_remember_when_rejected() {
        val engine = FakeExperienceEngine(experience)
        val decisionEngine = FakeDecisionEngine(
            RuntimeExperienceDecision(
                shouldRemember = false,
                reason = "Experience has low importance"
            )
        )
        val store = FakeExperienceStore()

        val pipeline = DefaultRuntimeExperiencePipeline(
            experienceEngine = engine,
            decisionEngine = decisionEngine,
            experienceStore = store
        )

        val result = pipeline.process(context)

        assertEquals(1, engine.calls)
        assertEquals(1, decisionEngine.calls)
        assertSame(experience, decisionEngine.received)
        assertEquals(emptyList(), store.stored)
        assertSame(experience, result.experience)
        assertEquals(false, result.decision.shouldRemember)
    }
}
