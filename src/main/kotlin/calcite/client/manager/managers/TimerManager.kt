package calcite.client.manager.managers

import calcite.client.event.events.RunGameLoopEvent
import calcite.client.manager.Manager
import calcite.client.mixin.extension.tickLength
import calcite.client.mixin.extension.timer
import calcite.client.module.AbstractModule
import calcite.client.util.TickTimer
import calcite.client.util.TimeUnit
import calcite.commons.extension.synchronized
import calcite.event.listener.listener
import java.util.*

object TimerManager : Manager {
    private val timer = TickTimer(TimeUnit.TICKS)
    private val modifications = TreeMap<AbstractModule, Pair<Float, Long>>(compareByDescending { it.modulePriority }).synchronized() // <Module, <Tick length, Added Time>>

    private var modified = false

    var tickLength = 50.0f; private set

    init {
        listener<RunGameLoopEvent.Start> {
            if (timer.tick(5L)) {
                val removeTime = System.currentTimeMillis() - 250L
                modifications.values.removeIf { it.second < removeTime }
            }

            if (mc.player != null && modifications.isNotEmpty()) {
                modifications.firstEntry()?.let {
                    mc.timer.tickLength = it.value.first
                }
                modified = true
            } else if (modified) {
                reset()
            }

            tickLength = mc.timer.tickLength
        }
    }

    fun AbstractModule.resetTimer() {
        modifications.remove(this)
    }

    fun AbstractModule.modifyTimer(tickLength: Float) {
        if (mc.player != null) {
            modifications[this] = tickLength to System.currentTimeMillis()
        }
    }

    private fun reset() {
        mc.timer.tickLength = 50.0f
        modified = false
    }
}