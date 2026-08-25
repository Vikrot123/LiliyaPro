package pro.liliya.core.runtime.intelligence.context.cognitive.lifecycle

interface CognitiveContextLifecycle {

    fun start()

    fun stop()

    fun isStarted(): Boolean

    fun reset()
}
