package pro.liliya.core.runtime.intelligence.context

import pro.liliya.core.runtime.RuntimeServiceRegistry

class DefaultRuntimeContextProvider(
    private val registry: RuntimeServiceRegistry
) : RuntimeContextProvider {

    override fun currentContext(): RuntimeContext {
        return object : RuntimeContext {

            override fun snapshot(): RuntimeContextSnapshot {
                return RuntimeContextSnapshot(
                    runtimeState = "UNKNOWN",
                    activeServices = registry.getStates().keys.toList(),
                    timestamp = System.currentTimeMillis()
                )
            }
        }
    }
}
