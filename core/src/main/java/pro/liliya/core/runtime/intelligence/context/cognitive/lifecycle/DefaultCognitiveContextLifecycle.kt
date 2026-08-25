package pro.liliya.core.runtime.intelligence.context.cognitive.lifecycle

class DefaultCognitiveContextLifecycle :
    CognitiveContextLifecycle {

    private var started = false

    override fun start() {
        started = true
    }

    override fun stop() {
        started = false
    }

    override fun isStarted(): Boolean {
        return started
    }

    override fun reset() {
        started = false
    }
}
