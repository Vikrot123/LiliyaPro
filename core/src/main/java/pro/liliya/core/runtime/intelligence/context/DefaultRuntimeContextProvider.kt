package pro.liliya.core.runtime.intelligence.context

import pro.liliya.core.CoreRuntimeStateHolder

import pro.liliya.core.runtime.RuntimeServiceRegistry

class DefaultRuntimeContextProvider(
    private val registry: RuntimeServiceRegistry,
    private val runtimeStateHolder: CoreRuntimeStateHolder
) : RuntimeContextProvider {

    override fun currentContext(): RuntimeContext {
        return object : RuntimeContext {

            override fun snapshot(): RuntimeContextSnapshot {
                return RuntimeContextSnapshot(
                    runtimeState = runtimeStateHolder.state().name,
                    activeServices = registry.getStates()
                        .filterValues { it == pro.liliya.core.runtime.RuntimeServiceState.RUNNING }
                        .keys
                        .toList(),
                    timestamp = System.currentTimeMillis(),
                    moduleStates = runtimeStateHolder.moduleStates(),
                    failureReason = runtimeStateHolder.failureReason()
                )
            }
        }
    }
}
