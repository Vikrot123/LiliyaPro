package pro.liliya.core.module

interface LiliyaModule {

    val descriptor: ModuleDescriptor

    val name: String
        get() = descriptor.name

    var state: ModuleState

    fun init()

    fun start()

    fun stop()
}
