package pro.liliya.core.runtime.observer

import pro.liliya.core.RuntimeEvent

interface RuntimeObserver {

    fun onRuntimeEvent(event: RuntimeEvent)
}
