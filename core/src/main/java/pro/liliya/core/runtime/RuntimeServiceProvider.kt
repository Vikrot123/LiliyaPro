package pro.liliya.core.runtime

interface RuntimeServiceProvider {

    fun provideServices(): List<RuntimeService>

}
