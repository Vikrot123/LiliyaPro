package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Test
import pro.liliya.core.runtime.RuntimeServiceBootstrap
import pro.liliya.core.runtime.RuntimeServiceBootstrapHolder
import pro.liliya.core.runtime.RuntimeServiceProvider
import pro.liliya.core.runtime.RuntimeServiceRegistry

class RuntimeServiceBootstrapHolderContractTest {

    @Test
    fun replace_stopsOldBootstrapAndInstallsNewOne() {
        val registry = RuntimeServiceRegistry()

        val provider = object : RuntimeServiceProvider {
            override fun provideServices() = emptyList<pro.liliya.core.runtime.RuntimeService>()
        }

        val first = RuntimeServiceBootstrap(
            provider,
            registry
        )

        val second = RuntimeServiceBootstrap(
            provider,
            registry
        )

        first.start()

        val holder = RuntimeServiceBootstrapHolder {
            RuntimeServiceBootstrap(
                provider,
                registry
            )
        }

        holder.replace(first)

        assertEquals(first, holder.get())

        holder.replace(second)

        assertEquals(second, holder.get())
        assertNotSame(first, holder.get())
    }

    @Test
    fun reset_createsNewBootstrapInstance() {
        val registry = RuntimeServiceRegistry()

        val provider = object : RuntimeServiceProvider {
            override fun provideServices() = emptyList<pro.liliya.core.runtime.RuntimeService>()
        }

        val holder = RuntimeServiceBootstrapHolder {
            RuntimeServiceBootstrap(
                provider,
                registry
            )
        }

        val old = holder.get()

        holder.reset()

        val new = holder.get()

        assertNotSame(old, new)
    }
}
