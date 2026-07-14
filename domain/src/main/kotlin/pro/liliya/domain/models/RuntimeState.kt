package pro.liliya.domain.models

/**
 * Глобальное состояние Runtime LiliyaPro.
 */
enum class RuntimeState {

    /**
     * Создание объектов.
     */
    BOOTING,

    /**
     * Инициализация компонентов.
     */
    INITIALIZING,

    /**
     * Система готова к работе.
     */
    READY,

    /**
     * Ожидание пользовательского ввода.
     */
    LISTENING,

    /**
     * Выполняется обработка запроса.
     */
    THINKING,

    /**
     * Формирование ответа.
     */
    RESPONDING,

    /**
     * Работа временно приостановлена.
     */
    PAUSED,

    /**
     * Возникла ошибка.
     */
    ERROR,

    /**
     * Работа завершена.
     */
    STOPPED
}
