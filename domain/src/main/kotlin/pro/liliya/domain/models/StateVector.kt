package pro.liliya.domain.models

data class StateVector(
    val emotionalState: Float = 0f,
    val attentionLevel: Float = 1f,
    val cognitiveLoad: Float = 0f,
    val timestamp: Long = System.currentTimeMillis()
) {

    init {
        require(emotionalState in -1f..1f) {
            "emotionalState must be between -1 and 1"
        }

        require(attentionLevel in 0f..1f) {
            "attentionLevel must be between 0 and 1"
        }

        require(cognitiveLoad in 0f..1f) {
            "cognitiveLoad must be between 0 and 1"
        }
    }
}
