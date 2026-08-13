package pro.liliya.core.runtime.control

interface RuntimeControl {

    fun execute(
        command: RuntimeCommand
    ): RuntimeControlResult

}
