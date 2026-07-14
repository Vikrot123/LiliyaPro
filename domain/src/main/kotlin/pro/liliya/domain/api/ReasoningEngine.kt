package pro.liliya.domain.api

import pro.liliya.domain.models.ReasoningResult

/**
 * Движок рассуждений LiliyaPro.
 *
 * Отвечает за анализ запроса пользователя,
 * использование памяти,
 * построение логической цепочки
 * и подготовку результата рассуждения.
 */
interface ReasoningEngine {

    /**
     * Выполнить рассуждение.
     */
    suspend fun reason(
        input: String
    ): ReasoningResult
}
