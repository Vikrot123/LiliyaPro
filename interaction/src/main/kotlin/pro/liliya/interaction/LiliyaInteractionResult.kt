package pro.liliya.interaction

data class LiliyaInteractionResult(
    val source: String,
    val interpretation: String,
    val confidence: Double,
    val experienceCommitted: Boolean,
    val knowledgeProduced: Boolean
)
