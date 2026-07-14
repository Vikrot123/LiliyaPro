package pro.liliya.domain.models

data class ReasoningResult(

    val summary: String,

    val confidence: Float,

    val facts: List<String> = emptyList(),

    val recommendations: List<String> = emptyList()
)
