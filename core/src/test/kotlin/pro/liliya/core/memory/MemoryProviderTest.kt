package pro.liliya.core.memory

import kotlinx.coroutines.runBlocking
import pro.liliya.domain.models.Episode
import kotlin.test.Test
import kotlin.test.assertEquals

class MemoryProviderTest {

    @Test
    fun `memory should store episodes`() = runBlocking {

        val memory = InMemoryMemoryProvider()

        val episode = Episode(
            id = "episode-1",
            events = emptyList()
        )

        memory.saveEpisode(episode)

        val result = memory.loadEpisodes()

        assertEquals(
            1,
            result.size
        )

        assertEquals(
            "episode-1",
            result.first().id
        )
    }
}
