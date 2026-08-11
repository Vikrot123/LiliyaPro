package pro.liliya.core

import org.junit.jupiter.api.Test
import pro.liliya.core.module.ModuleManager
import pro.liliya.core.module.ModuleRegistry
import pro.liliya.core.module.ModuleState
import pro.liliya.core.module.ModuleProvider
import pro.liliya.core.module.ModuleDescriptor


class ModuleFailureTest {


    @Test
    fun failedModuleDoesNotStopCore() {


        val registry = ModuleRegistry()


        val provider = object : ModuleProvider {

            override fun provideModules() =
                listOf(
                    CoreTestModule(),
                    FailingModule()
                )
        }


        val manager = ModuleManager(
            registry,
            provider
        )


        manager.loadModules()

        manager.startModules()


        val states = registry.getStates()


        require(
            states["CORE_TEST_MODULE"] == ModuleState.RUNNING
        ) {
            "Core module should keep running"
        }


        require(
            states["FAILING_MODULE"] == ModuleState.FAILED
        ) {
            "Failed module should be FAILED"
        }


        manager.stopModules()
    }
}


class CoreTestModule : pro.liliya.core.module.LiliyaModule {

    override val descriptor = ModuleDescriptor(
        name = "CORE_TEST_MODULE",
        version = "0.3",
        critical = true
    )

    override var state = ModuleState.CREATED

    override fun init() {
        state = ModuleState.INITIALIZED
    }

    override fun start() {
        state = ModuleState.RUNNING
    }

    override fun stop() {
        state = ModuleState.STOPPED
    }
}
