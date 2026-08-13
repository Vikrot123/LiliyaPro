package pro.liliya.core.runtime.control

class RuntimeControlRegistry {

    private val controls =
        mutableMapOf<Class<*>, RuntimeControl>()

    fun register(
        control: RuntimeControl
    ) {
        controls[control::class.java] = control
    }

    fun get(
        type: Class<*>
    ): RuntimeControl? {
        return controls[type]
    }

    fun clear() {
        controls.clear()
    }
}
