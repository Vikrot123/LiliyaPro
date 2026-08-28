package pro.liliya.interaction

internal interface LiliyaInteractionLifecyclePort {

    fun start()

    fun stop()

    fun state(): LiliyaInteractionRuntimeState
}
