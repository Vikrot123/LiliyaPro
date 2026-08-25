package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelectionCriteria

class CognitiveContextSelectionCriteriaContractTest {

    @Test
    fun default_criteria_should_represent_unrestricted_selection() {
        val criteria = CognitiveContextSelectionCriteria()

        assertEquals(0.0, criteria.minimumRelevance)
        assertEquals(0.0, criteria.minimumImportance)
        assertEquals(0.0, criteria.minimumConfidence)
        assertEquals(null, criteria.maximumAgeMillis)
    }

    @Test
    fun zero_thresholds_should_be_valid() {
        val criteria = CognitiveContextSelectionCriteria(
            minimumRelevance = 0.0,
            minimumImportance = 0.0,
            minimumConfidence = 0.0,
            maximumAgeMillis = 0L
        )

        assertEquals(0.0, criteria.minimumRelevance)
        assertEquals(0.0, criteria.minimumImportance)
        assertEquals(0.0, criteria.minimumConfidence)
        assertEquals(0L, criteria.maximumAgeMillis)
    }

    @Test
    fun fractional_thresholds_should_be_preserved_exactly() {
        val criteria = CognitiveContextSelectionCriteria(
            minimumRelevance = 0.25,
            minimumImportance = 0.50,
            minimumConfidence = 0.75,
            maximumAgeMillis = 1_500L
        )

        assertEquals(0.25, criteria.minimumRelevance)
        assertEquals(0.50, criteria.minimumImportance)
        assertEquals(0.75, criteria.minimumConfidence)
        assertEquals(1_500L, criteria.maximumAgeMillis)
    }

    @Test
    fun criteria_should_be_value_based() {
        val first = CognitiveContextSelectionCriteria(
            minimumRelevance = 0.7,
            minimumImportance = 0.6,
            minimumConfidence = 0.8,
            maximumAgeMillis = 10_000L
        )

        val second = CognitiveContextSelectionCriteria(
            minimumRelevance = 0.7,
            minimumImportance = 0.6,
            minimumConfidence = 0.8,
            maximumAgeMillis = 10_000L
        )

        assertEquals(first, second)
        assertEquals(first.hashCode(), second.hashCode())
    }

    @Test
    fun maximum_age_should_be_optional() {
        val criteria = CognitiveContextSelectionCriteria(
            minimumRelevance = 0.5,
            minimumImportance = 0.5,
            minimumConfidence = 0.5
        )

        assertTrue(criteria.maximumAgeMillis == null)
    }
}
