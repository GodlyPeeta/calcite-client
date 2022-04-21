package calcite.client.gui

import kotlinx.coroutines.Deferred
import calcite.client.event.KamiEventBus
import calcite.client.gui.clickgui.CalciteClickGui
import calcite.client.gui.hudgui.AbstractHudElement
import calcite.client.gui.hudgui.CalciteHudGui
import calcite.client.util.AsyncCachedValue
import calcite.client.util.StopTimer
import calcite.client.util.TimeUnit
import calcite.commons.collections.AliasSet
import calcite.commons.utils.ClassUtils
import calcite.commons.utils.ClassUtils.instance
import java.lang.reflect.Modifier

internal object GuiManager : calcite.client.AsyncLoader<List<Class<out AbstractHudElement>>> {
    override var deferred: Deferred<List<Class<out AbstractHudElement>>>? = null
    private val hudElementSet = AliasSet<AbstractHudElement>()

    val hudElements by AsyncCachedValue(5L, TimeUnit.SECONDS) {
        hudElementSet.distinct().sortedBy { it.name }
    }

    override fun preLoad0(): List<Class<out AbstractHudElement>> {
        val stopTimer = StopTimer()

        val list = ClassUtils.findClasses<AbstractHudElement>("calcite.client.gui.hudgui.elements") {
            filter { Modifier.isFinal(it.modifiers) }
        }

        val time = stopTimer.stop()

        calcite.client.CalciteMod.LOG.info("${list.size} hud elements found, took ${time}ms")
        return list
    }

    override fun load0(input: List<Class<out AbstractHudElement>>) {
        val stopTimer = StopTimer()

        for (clazz in input) {
            register(clazz.instance)
        }

        val time = stopTimer.stop()
        calcite.client.CalciteMod.LOG.info("${input.size} hud elements loaded, took ${time}ms")

        CalciteClickGui.onGuiClosed()
        CalciteHudGui.onGuiClosed()

        KamiEventBus.subscribe(CalciteClickGui)
        KamiEventBus.subscribe(CalciteHudGui)
    }

    internal fun register(hudElement: AbstractHudElement) {
        hudElementSet.add(hudElement)
        CalciteHudGui.register(hudElement)
    }

    internal fun unregister(hudElement: AbstractHudElement) {
        hudElementSet.remove(hudElement)
        CalciteHudGui.unregister(hudElement)
    }

    fun getHudElementOrNull(name: String?) = name?.let { hudElementSet[it] }
}