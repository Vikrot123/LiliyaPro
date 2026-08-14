package pro.liliya.core.module

class ModuleManagerHolder {

    private var currentManager: ModuleManager? = null

    fun get(): ModuleManager? {
        return currentManager
    }

    fun set(manager: ModuleManager) {
        currentManager = manager
    }

    fun clear() {
        currentManager = null
    }
}
