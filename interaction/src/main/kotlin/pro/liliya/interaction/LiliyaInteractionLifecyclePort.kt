package pro.liliya.interaction

interface LiliyaInteractionLifecyclePort {

    fun start()

    fun stop()

    fun state(): LiliyaInteractionRuntimeState
}
