package pro.liliya.interaction

internal object LiliyaInteractionSource {

    fun normalize(
        source: String
    ): String {
        val normalized = source.trim()

        require(normalized.isNotEmpty()) {
            "Interaction source must not be blank"
        }

        return normalized
    }
}
