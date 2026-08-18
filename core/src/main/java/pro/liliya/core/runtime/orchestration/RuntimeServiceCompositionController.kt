package pro.liliya.core.runtime.orchestration

import pro.liliya.core.runtime.RuntimeServiceBootstrap
import pro.liliya.core.runtime.RuntimeServiceProvider

interface RuntimeServiceCompositionController {

    fun setRuntimeServiceProvider(
        provider: RuntimeServiceProvider
    )

    fun resetRuntimeServiceProvider()

    fun replaceRuntimeServiceBootstrap(
        bootstrap: RuntimeServiceBootstrap
    )

    fun configureRuntimeServiceProvider(
        provider: RuntimeServiceProvider
    )

    fun resetRuntimeServiceConfiguration()
}
