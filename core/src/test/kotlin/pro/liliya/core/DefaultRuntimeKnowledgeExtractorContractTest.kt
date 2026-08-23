package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.experience.consolidation.RuntimeExperienceConsolidation
import pro.liliya.core.runtime.intelligence.knowledge.DefaultRuntimeKnowledgeExtractor
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource

class DefaultRuntimeKnowledgeExtractorContractTest {

    @Test
    fun consolidation_creates_knowledge() {

        val knowledge =
            DefaultRuntimeKnowledgeExtractor()
                .extract(
                    RuntimeExperienceConsolidation(
                        summary = "Runtime pattern detected",
                        processedCount = 3,
                        createdAt = 1L
                    )
                )

        assertEquals(
            "Runtime pattern detected",
            knowledge.statement
        )

        assertEquals(
            0.8,
            knowledge.confidence
        )

        assertEquals(
            RuntimeKnowledgeSource.CONSOLIDATION,
            knowledge.source
        )
    }

    @Test
    fun empty_consolidation_has_zero_confidence() {

        val knowledge =
            DefaultRuntimeKnowledgeExtractor()
                .extract(
                    RuntimeExperienceConsolidation(
                        summary = "No experiences available",
                        processedCount = 0,
                        createdAt = 1L
                    )
                )

        assertEquals(
            0.0,
            knowledge.confidence
        )
    }
}
