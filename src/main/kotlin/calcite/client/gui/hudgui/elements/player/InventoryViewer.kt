package calcite.client.gui.hudgui.elements.player

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import calcite.client.event.SafeClientEvent
import calcite.client.gui.hudgui.HudElement
import calcite.client.util.color.ColorHolder
import calcite.client.util.graphics.GlStateUtils
import calcite.client.util.graphics.RenderUtils2D
import calcite.client.util.graphics.VertexHelper
import calcite.client.util.items.storageSlots
import calcite.client.util.math.Vec2d
import calcite.client.util.threads.runSafe
import org.lwjgl.opengl.GL11.*

internal object InventoryViewer : HudElement(
    name = "InventoryViewer",
    category = Category.PLAYER,
    description = "Items in Inventory"
) {
    private val mcTexture by setting("Minecraft Texture (why would you use this)", false)
    private val border by setting("Border", true, { !mcTexture })
    private val borderColor by setting("Border Color", ColorHolder(111, 166, 222, 255), true, { !mcTexture && border })
    private val background by setting("Background", true, { !mcTexture })
    private val backgroundColor by setting("Background Color", ColorHolder(30, 36, 48, 127), true, { !mcTexture && background })

    private val containerTexture = ResourceLocation("textures/gui/container/inventory.png")

    override val hudWidth: Float = 162.0f
    override val hudHeight: Float = 54.0f

    override fun renderHud(vertexHelper: VertexHelper) {
        super.renderHud(vertexHelper)
        runSafe {
            drawFrame(vertexHelper)
            drawFrameTexture()
            drawItems()
        }
    }

    private fun drawFrame(vertexHelper: VertexHelper) {
        if (!mcTexture) {
            if (background) {
                RenderUtils2D.drawRectFilled(vertexHelper, posEnd = Vec2d(162.0, 54.0), color = backgroundColor)
            }
            if (border) {
                RenderUtils2D.drawRectOutline(vertexHelper, posEnd = Vec2d(162.0, 54.0), lineWidth = 2.0f, color = borderColor)
            }
        }
    }

    private fun drawFrameTexture() {
        val tessellator = Tessellator.getInstance()
        val buffer = tessellator.buffer
        GlStateUtils.texture2d(true)

        if (mcTexture) {
            mc.renderEngine.bindTexture(containerTexture)
            buffer.begin(GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX)
            buffer.pos(0.0, 0.0, 0.0).tex(0.02734375, 0.32421875).endVertex() // (7 / 256), (83 / 256)
            buffer.pos(0.0, 54.0, 0.0).tex(0.02734375, 0.53125).endVertex() // (7 / 256), (136 / 256)
            buffer.pos(162.0, 0.0, 0.0).tex(0.65625, 0.32421875).endVertex() // (168 / 256), (83 / 256)
            buffer.pos(162.0, 54.0, 0.0).tex(0.65625, 0.53125).endVertex() // (168 / 256), (136 / 256)
            tessellator.draw()
        }
    }

    private fun SafeClientEvent.drawItems() {
        for ((index, slot) in player.storageSlots.withIndex()) {
            val itemStack = slot.stack
            if (itemStack.isEmpty) continue

            val slotX = index % 9 * 18 + 1
            val slotY = index / 9 * 18 + 1

            RenderUtils2D.drawItem(itemStack, slotX, slotY)
        }
    }

}