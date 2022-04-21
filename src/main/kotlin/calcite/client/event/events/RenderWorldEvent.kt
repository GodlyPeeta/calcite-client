package calcite.client.event.events

import calcite.client.event.Event
import calcite.client.event.ProfilerEvent
import calcite.client.mixin.extension.renderPosX
import calcite.client.mixin.extension.renderPosY
import calcite.client.mixin.extension.renderPosZ
import calcite.client.util.Wrapper
import calcite.client.util.graphics.CalciteTessellator

class RenderWorldEvent : Event, ProfilerEvent {
    override val profilerName: String = "kbRender3D"

    init {
        CalciteTessellator.buffer.setTranslation(
            -Wrapper.minecraft.renderManager.renderPosX,
            -Wrapper.minecraft.renderManager.renderPosY,
            -Wrapper.minecraft.renderManager.renderPosZ
        )
    }
}