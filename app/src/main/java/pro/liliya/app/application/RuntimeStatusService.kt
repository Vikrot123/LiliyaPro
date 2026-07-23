package pro.liliya.app.application

import pro.liliya.domain.models.RuntimeState

class RuntimeStatusService {

    private val runtime =
        AppContainer.runtime


    fun currentState(): RuntimeState {

        return runtime
            .stateMachine()
            .state()

    }
}
