package calcite.client.gui.hudgui

import net.minecraftforge.fml.common.gameevent.InputEvent
import calcite.client.event.events.RenderOverlayEvent
import calcite.client.gui.AbstractCalciteGui
import calcite.client.gui.clickgui.CalciteClickGui
import calcite.client.gui.hudgui.component.HudButton
import calcite.client.gui.hudgui.elements.client.WaterMark
import calcite.client.gui.hudgui.window.HudSettingWindow
import calcite.client.gui.rgui.Component
import calcite.client.gui.rgui.windows.ListWindow
import calcite.client.module.modules.client.ClickGUI
import calcite.client.module.modules.client.Hud
import calcite.client.module.modules.client.HudEditor
import calcite.client.util.graphics.GlStateUtils
import calcite.client.util.graphics.VertexHelper
import calcite.client.util.math.Vec2f
import calcite.event.listener.listener
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11.*
import java.util.*

object CalciteHudGui : AbstractCalciteGui<HudSettingWindow, AbstractHudElement>() {

    override val alwaysTicking = true
    private val hudWindows = EnumMap<AbstractHudElement.Category, ListWindow>(AbstractHudElement.Category::class.java)

    init {
        var posX = 0.0f
        var posY = 0.0f
        val screenWidth = CalciteClickGui.mc.displayWidth / ClickGUI.getScaleFactorFloat()

        for (category in AbstractHudElement.Category.values()) {
            val window = ListWindow(category.displayName, posX, 0.0f, 90.0f, 300.0f, Component.SettingGroup.HUD_GUI)
            windowList.add(window)
            hudWindows[category] = window

            posX += 90.0f

            if (posX > screenWidth) {
                posX = 0.0f
                posY += 100.0f
            }
        }

        listener<InputEvent.KeyInputEvent> {
            val eventKey = Keyboard.getEventKey()

            if (eventKey == Keyboard.KEY_NONE || Keyboard.isKeyDown(Keyboard.KEY_F3)) return@listener

            for (child in windowList) {
                if (child !is AbstractHudElement) continue
                if (!child.bind.isDown(eventKey)) continue
                child.visible = !child.visible
            }
        }
    }

    internal fun register(hudElement: AbstractHudElement) {
        val button = HudButton(hudElement)
        hudWindows[hudElement.category]!!.children.add(button)
        windowList.add(hudElement)
    }

    internal fun unregister(hudElement: AbstractHudElement) {
        hudWindows[hudElement.category]!!.children.removeIf { it is HudButton && it.hudElement == hudElement }
        windowList.remove(hudElement)
    }

    override fun onGuiClosed() {
        super.onGuiClosed()
        setHudButtonVisibility { true }
    }

    override fun newSettingWindow(element: AbstractHudElement, mousePos: Vec2f): HudSettingWindow {
        return HudSettingWindow(element, mousePos.x, mousePos.y)
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (keyCode == Keyboard.KEY_ESCAPE || HudEditor.bind.value.isDown(keyCode) && !searching && settingWindow?.listeningChild == null) {
            HudEditor.disable()
        } else {
            super.keyTyped(typedChar, keyCode)

            val string = typedString.replace(" ", "")

            if (string.isNotEmpty()) {
                setHudButtonVisibility { hudButton ->
                    hudButton.hudElement.componentName.contains(string, true)
                        || hudButton.hudElement.alias.any { it.contains(string, true) }
                }
            } else {
                setHudButtonVisibility { true }
            }
        }
    }

    private fun setHudButtonVisibility(function: (HudButton) -> Boolean) {
        windowList.filterIsInstance<ListWindow>().forEach {
            for (child in it.children) {
                if (child !is HudButton) continue
                child.visible = function(child)
            }
        }
    }

    init {
        listener<RenderOverlayEvent>(0) {
            if (mc?.world == null || mc?.player == null || mc?.currentScreen == this || mc?.gameSettings?.showDebugInfo != false) return@listener

            val vertexHelper = VertexHelper(GlStateUtils.useVbo())
            GlStateUtils.rescaleKami()

            if (Hud.isEnabled) {
                for (window in windowList) {
                    if (window !is AbstractHudElement || !window.visible) continue
                    renderHudElement(vertexHelper, window)
                }
            } else if (WaterMark.visible) {
                renderHudElement(vertexHelper, WaterMark)
            }

            GlStateUtils.rescaleMc()
            GlStateUtils.depth(true)
        }
    }

    private fun renderHudElement(vertexHelper: VertexHelper, window: AbstractHudElement) {
        glPushMatrix()
        glTranslatef(window.renderPosX, window.renderPosY, 0.0f)

        if (Hud.hudFrame) window.renderFrame(vertexHelper)

        glScalef(window.scale, window.scale, window.scale)
        window.renderHud(vertexHelper)

        glPopMatrix()
    }

}