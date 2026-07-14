package pro.liliya.core.memory

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import pro.liliya.domain.api.MemoryProvider
import pro.liliya.domain.models.Episode

class InMemoryMemoryProvider : MemoryProvider {

    private val mutex = Mutex()

    private val episodes = mutableListOf<Episode>()

    override suspend fun saveEpisode(
        episode: Episode
    ) {
        mutex.withLock {
            episodes.add(episode)
        }
    }

    override suspend fun loadEpisodes(): List<Episode> {

        return mutex.withLock {
            episodes.toList()
        }
    }
}
