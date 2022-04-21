package calcite.client.manager

import calcite.client.util.Wrapper

interface Manager {
    val mc get() = Wrapper.minecraft
}