package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperience
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceImportance
import pro.liliya.core.runtime.intelligence.experience.consolidation.DefaultRuntimeExperienceConsolidator
import pro.liliya.core.runtime.intelligence.knowledge.DefaultRuntimeKnowledgeExtractor
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeExperienceKnowledgeSemanticBridgeContractTest {

    @Test
    fun latest_experience_semantics_must_reach_knowledge_statement() {
        val experience =
            RuntimeExperience(
                description = "runtime recovered and remained stable",
                meaning =
                    RuntimeMeaningResult(
                        interpretation =
                            "runtime recovered and remained stable",
                        confidence = 0.9,
                        significance =
                            RuntimeMeaningSignificance.STABLE,
                        generatedAt = 1L
                    ),
                importance =
                    RuntimeExperienceImportance.HIGH,
                createdAt = 1L
            )

        val consolidation =
            DefaultRuntimeExperienceConsolidator()
                .consolidate(
                    listOf(experience)
                )

        val knowledge =
            DefaultRuntimeKnowledgeExtractor()
                .extract(consolidation)

        assertTrue(
            knowledge.statement.contains(
                experience.description
            ),
            "experience semantics must survive into knowledge statement"
        )
    }
}
