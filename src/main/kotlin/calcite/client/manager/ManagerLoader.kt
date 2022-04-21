package calcite.client.manager

import kotlinx.coroutines.Deferred
import calcite.client.AsyncLoader
import calcite.client.CalciteMod
import calcite.client.event.KamiEventBus
import calcite.client.util.StopTimer
import calcite.commons.utils.ClassUtils
import calcite.commons.utils.ClassUtils.instance

internal object ManagerLoader : calcite.client.AsyncLoader<List<Class<out Manager>>> {
    override var deferred: Deferred<List<Class<out Manager>>>? = null

    override fun preLoad0(): List<Class<out Manager>> {
        val stopTimer = StopTimer()

        val list = ClassUtils.findClasses<Manager>("calcite.client.manager.managers")

        val time = stopTimer.stop()

        calcite.client.CalciteMod.LOG.info("${list.size} managers found, took ${time}ms")
        return list
    }

    override fun load0(input: List<Class<out Manager>>) {
        val stopTimer = StopTimer()

        for (clazz in input) {
            KamiEventBus.subscribe(clazz.instance)
        }

        val time = stopTimer.stop()
        calcite.client.CalciteMod.LOG.info("${input.size} managers loaded, took ${time}ms")
    }
}