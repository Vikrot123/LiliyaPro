package pro.liliya.core.runtime.intelligence.meaning

data class RuntimeMeaningResult(
    val interpretation: String,
    val confidence: Double,
    val significance: RuntimeMeaningSignificance,
    val generatedAt: Long
)
