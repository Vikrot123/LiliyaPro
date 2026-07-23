package pro.liliya.app.application

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class LiliyaApplication : Application() {

    private val applicationScope =
        CoroutineScope(
            SupervisorJob() +
            Dispatchers.Default
        )


    override fun onCreate() {
        super.onCreate()

        applicationScope.launch {

            AppContainer.runtime.start()

        }
    }


    override fun onTerminate() {
        super.onTerminate()

        applicationScope.launch {

            AppContainer.runtime.stop()

        }
    }
}
