package pro.liliya.core.runtime

interface RuntimeService {

    val name: String

    val state: RuntimeServiceState

    fun start()

    fun stop()
}
