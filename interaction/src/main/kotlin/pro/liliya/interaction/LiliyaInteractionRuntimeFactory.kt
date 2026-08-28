package pro.liliya.interaction

import pro.liliya.core.CoreRuntime

object LiliyaInteractionRuntimeFactory {

    fun create(): LiliyaInteractionRuntime {
        val corePort =
            CoreRuntimeInteractionPort(
                CoreRuntime
            )

        return LiliyaInteractionRuntime(
            gateway =
                LiliyaInteractionGateway(
                    corePort
                ),
            lifecycle = corePort
        )
    }
}
