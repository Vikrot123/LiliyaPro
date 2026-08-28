package pro.liliya.interaction

class LiliyaInteractionGateway(
    private val port: LiliyaInteractionPort
) {

    fun process(
        request: LiliyaInteractionRequest
    ): LiliyaInteractionResult {
        val source =
            LiliyaInteractionSource.normalize(
                request.source
            )

        return port.process(
            source = source,
            authority = request.authority
        )
    }
}
