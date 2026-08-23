package pro.liliya.core.runtime.intelligence.selfmodel

import pro.liliya.core.runtime.intelligence.context.RuntimeContextMetadata
import pro.liliya.core.runtime.intelligence.context.RuntimeContextProvider

class DefaultRuntimeSelfModelProvider(
    private val contextProvider: RuntimeContextProvider,
    private val metadataProvider: () -> RuntimeContextMetadata
) : RuntimeSelfModelProvider {

    override fun currentSelfModel(): RuntimeSelfModel {

        return RuntimeSelfModel(
            snapshot = contextProvider
                .currentContext()
                .snapshot(),

            metadata = metadataProvider()
        )
    }
}
