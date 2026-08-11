package pro.liliya.core.module

class CoreModuleProvider : ModuleProvider {

    override fun provideModules(): List<LiliyaModule> {

        return listOf(
            CoreModule()
        )
    }
}
