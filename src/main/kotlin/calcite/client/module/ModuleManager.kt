package calcite.client.module

import kotlinx.coroutines.Deferred
import calcite.client.AsyncLoader
import calcite.client.CalciteMod
import calcite.client.event.KamiEventBus
import calcite.client.util.AsyncCachedValue
import calcite.client.util.StopTimer
import calcite.client.util.TimeUnit
import calcite.commons.collections.AliasSet
import calcite.commons.utils.ClassUtils
import calcite.commons.utils.ClassUtils.instance
import org.lwjgl.input.Keyboard
import java.lang.reflect.Modifier

object ModuleManager : calcite.client.AsyncLoader<List<Class<out AbstractModule>>> {
    override var deferred: Deferred<List<Class<out AbstractModule>>>? = null

    private val moduleSet = AliasSet<AbstractModule>()
    private val modulesDelegate = AsyncCachedValue(5L, TimeUnit.SECONDS) {
        moduleSet.distinct().sortedBy { it.name }
    }

    val modules by modulesDelegate

    override fun preLoad0(): List<Class<out AbstractModule>> {
        val stopTimer = StopTimer()

        val list = ClassUtils.findClasses<AbstractModule>("calcite.client.module.modules") {
            filter { Modifier.isFinal(it.modifiers) }
        }

        val time = stopTimer.stop()

        calcite.client.CalciteMod.LOG.info("${list.size} modules found, took ${time}ms")
        return list
    }

    override fun load0(input: List<Class<out AbstractModule>>) {
        val stopTimer = StopTimer()

        for (clazz in input) {
            register(clazz.instance)
        }

        val time = stopTimer.stop()
        calcite.client.CalciteMod.LOG.info("${input.size} modules loaded, took ${time}ms")
    }

    internal fun register(module: AbstractModule) {
        moduleSet.add(module)
        if (module.enabledByDefault || module.alwaysEnabled) module.enable()
        if (module.alwaysListening) KamiEventBus.subscribe(module)

        modulesDelegate.update()
    }

    internal fun unregister(module: AbstractModule) {
        moduleSet.remove(module)
        KamiEventBus.unsubscribe(module)

        modulesDelegate.update()
    }

    internal fun onBind(eventKey: Int) {
        if (Keyboard.isKeyDown(Keyboard.KEY_F3)) return  // if key is the 'none' key (stuff like mod key in i3 might return 0)
        for (module in modules) {
            if (module.bind.value.isDown(eventKey)) module.toggle()
        }
    }

    fun getModuleOrNull(moduleName: String?) = moduleName?.let { moduleSet[it] }
}