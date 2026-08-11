package pro.liliya.core.module


interface LiliyaModule {

    val name: String

    var state: ModuleState


    fun init()


    fun start()


    fun stop()
}
