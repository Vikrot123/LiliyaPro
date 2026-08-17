package pro.liliya.core.runtime.module.composition

import pro.liliya.core.module.ModuleManager
import pro.liliya.core.module.ModuleManagerHolder
import pro.liliya.core.module.ModuleProvider
import pro.liliya.core.module.ModuleProviderHolder
import pro.liliya.core.module.CoreModuleProvider
import pro.liliya.core.module.ModuleRegistry
import pro.liliya.core.module.ModuleExceptionHandler
import pro.liliya.core.module.ModuleDependencyResolver
import pro.liliya.core.runtime.capability.RuntimeCapabilityInfrastructure
import pro.liliya.core.runtime.capability.DefaultRuntimeCapabilityInfrastructureProvider
import pro.liliya.core.runtime.orchestration.RuntimeModuleComposition

class DefaultRuntimeModuleComposition(
    private val owner: Any
) : RuntimeModuleComposition {

    private val moduleProvider: ModuleProvider =
        CoreModuleProvider()

    private val moduleProviderHolder =
        ModuleProviderHolder(moduleProvider)

    private val moduleManagerHolder =
        ModuleManagerHolder()

    private val capabilityInfrastructure: RuntimeCapabilityInfrastructure =
        DefaultRuntimeCapabilityInfrastructureProvider()
            .provide()

    private val moduleExceptionHandler =
        ModuleExceptionHandler()

    private val moduleDependencyResolver =
        ModuleDependencyResolver()

    override fun moduleExceptionHandler(): ModuleExceptionHandler {
        return moduleExceptionHandler
    }

    override fun moduleDependencyResolver(): ModuleDependencyResolver {
        return moduleDependencyResolver
    }

    override fun moduleProvider(): ModuleProvider {
        return moduleProvider
    }

    override fun moduleProviderHolder(): ModuleProviderHolder {
        return moduleProviderHolder
    }

    override fun setModuleProvider(
        provider: ModuleProvider
    ) {
        moduleProviderHolder.set(provider)
    }

    override fun resetModuleProvider() {
        moduleProviderHolder.reset()
    }

    override fun moduleManagerHolder(): ModuleManagerHolder {
        return moduleManagerHolder
    }

    override fun createModuleRegistry(): ModuleRegistry {
        return ModuleRegistry(
            capabilityInfrastructure,
            moduleExceptionHandler,
            moduleDependencyResolver
        )
    }

    override fun createModuleManager(
        registry: ModuleRegistry,
        provider: ModuleProvider
    ): ModuleManager {
        return ModuleManager(
            registry = registry,
            provider = provider
        )
    }

    override fun createModuleRuntime(): ModuleManager {
        return createModuleManager(
            registry = createModuleRegistry(),
            provider = moduleProviderHolder.get()
        )
    }

    override fun setModuleManager(manager: ModuleManager) {
        moduleManagerHolder.set(manager)
    }

    override fun moduleManager(): ModuleManager? {
        return moduleManagerHolder.get()
    }

    override fun setModuleStates(
        states: Map<String, pro.liliya.core.module.ModuleState>
    ) {
        // state ownership remains in runtime layer
    }

    override fun startModuleRuntime(
        manager: ModuleManager
    ) {
        moduleManagerHolder.set(manager)
    }

    override fun stopModuleRuntime(
        manager: ModuleManager
    ) {
        moduleManagerHolder.set(manager)
    }

    override fun clearModuleRuntime() {
        moduleManagerHolder.clear()
    }
}
