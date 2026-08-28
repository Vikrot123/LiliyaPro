package pro.liliya.interaction

internal interface LiliyaInteractionPort {

    fun process(
        source: String,
        authority: LiliyaInteractionAuthority
    ): LiliyaInteractionResult
}
