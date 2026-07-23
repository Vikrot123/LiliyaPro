package pro.liliya.app.application

import pro.liliya.runtime.LiliyaRuntime

/**
 * Центральный контейнер приложения.
 *
 * Пока он содержит только один экземпляр Runtime,
 * но в дальнейшем здесь будут создаваться и храниться
 * остальные сервисы приложения.
 */
object AppContainer {

    /**
     * Единственный экземпляр Runtime.
     */
    val runtime: LiliyaRuntime by lazy {
        LiliyaRuntime()
    }
}
