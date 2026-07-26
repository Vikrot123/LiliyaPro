package pro.liliya.domain.api

import kotlinx.coroutines.flow.Flow
import pro.liliya.domain.models.CognitiveEvent

/**
 * Главный интерфейс когнитивного движка LiliyaPro.
 *
 * Принимает пользовательский ввод
 * и возвращает поток событий когнитивного цикла.
 */
interface CognitiveEngine {

    /**
     * Запустить обработку пользовательского запроса.
     *
     * Возвращает события:
     * - анализ
     * - память
     * - рассуждение
     * - планирование
     * - генерация ответа
     */
    suspend fun process(
        input: String
    ): Flow<CognitiveEvent>
}
