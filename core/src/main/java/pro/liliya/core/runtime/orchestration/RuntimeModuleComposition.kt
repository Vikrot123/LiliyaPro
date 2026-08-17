package pro.liliya.core.runtime.orchestration

import pro.liliya.core.module.ModuleManager
import pro.liliya.core.module.ModuleProvider
import pro.liliya.core.module.ModuleProviderHolder
import pro.liliya.core.module.ModuleRegistry
import pro.liliya.core.module.ModuleExceptionHandler
import pro.liliya.core.module.ModuleDependencyResolver

interface RuntimeModuleComposition {

    fun moduleExceptionHandler(): ModuleExceptionHandler

    fun moduleDependencyResolver(): ModuleDependencyResolver

    fun moduleProvider(): ModuleProvider

    fun moduleProviderHolder(): ModuleProviderHolder

    fun setModuleProvider(
        provider: ModuleProvider
    )

    fun resetModuleProvider()

    fun moduleManagerHolder(): pro.liliya.core.module.ModuleManagerHolder

    fun createModuleRegistry(): ModuleRegistry

    fun createModuleManager(
        registry: ModuleRegistry,
        provider: ModuleProvider
    ): ModuleManager

    fun createModuleRuntime(): ModuleManager

    fun setModuleManager(manager: ModuleManager)

    fun moduleManager(): ModuleManager?
    fun setModuleStates(
        states: Map<String, pro.liliya.core.module.ModuleState>
    )


    fun startModuleRuntime(
        manager: ModuleManager
    )

    fun stopModuleRuntime(
        manager: ModuleManager
    )

    fun clearModuleRuntime()
}
