package pro.liliya.interaction

interface LiliyaInteractionPort {

    fun process(
        source: String,
        authority: LiliyaInteractionAuthority
    ): LiliyaInteractionResult
}
