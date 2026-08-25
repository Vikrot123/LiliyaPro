package pro.liliya.core.runtime.intelligence.context.cognitive.registry

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource

class DefaultCognitiveContextSourceRegistry :
    CognitiveContextSourceRegistry {

    private val registeredSources =
        mutableListOf<CognitiveContextSource>()

    override fun register(
        source: CognitiveContextSource
    ): Boolean {
        synchronized(registeredSources) {
            if (registeredSources.contains(source)) {
                return false
            }

            registeredSources += source
            return true
        }
    }

    override fun unregister(
        source: CognitiveContextSource
    ): Boolean {
        synchronized(registeredSources) {
            return registeredSources.remove(source)
        }
    }

    override fun sources(): List<CognitiveContextSource> {
        return synchronized(registeredSources) {
            registeredSources.toList()
        }
    }

    override fun clear() {
        synchronized(registeredSources) {
            registeredSources.clear()
        }
    }

    override fun size(): Int {
        return synchronized(registeredSources) {
            registeredSources.size
        }
    }
}
