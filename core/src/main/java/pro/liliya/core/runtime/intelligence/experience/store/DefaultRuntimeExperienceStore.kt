package pro.liliya.core.runtime.intelligence.experience.store

import pro.liliya.core.runtime.intelligence.experience.RuntimeExperience

class DefaultRuntimeExperienceStore :
    RuntimeExperienceStore {

    private val experiences =
        mutableListOf<RuntimeExperience>()

    override fun append(
        experience: RuntimeExperience
    ) {
        synchronized(experiences) {
            experiences += experience
        }
    }

    override fun remove(
        experience: RuntimeExperience
    ): Boolean {
        return synchronized(experiences) {
            val index =
                experiences.indexOfLast { stored ->
                    stored === experience
                }

            if (index < 0) {
                false
            } else {
                experiences.removeAt(index)
                true
            }
        }
    }

    override fun experiences(): List<RuntimeExperience> {
        return synchronized(experiences) {
            experiences.toList()
        }
    }
}
