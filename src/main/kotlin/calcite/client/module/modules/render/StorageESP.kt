package calcite.client.module.modules.render

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.minecraft.entity.Entity
import net.minecraft.entity.item.*
import net.minecraft.item.ItemShulkerBox
import net.minecraft.tileentity.*
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.fml.common.gameevent.TickEvent
import calcite.client.event.SafeClientEvent
import calcite.client.event.events.RenderWorldEvent
import calcite.client.module.Category
import calcite.client.module.Module
import calcite.commons.dataclasses.Quad
import calcite.client.util.color.ColorHolder
import calcite.client.util.color.DyeColors
import calcite.client.util.color.HueCycler
import calcite.client.util.graphics.ESPRenderer
import calcite.client.util.graphics.GeometryMasks
import calcite.client.util.threads.safeAsyncListener
import calcite.event.listener.listener

internal object StorageESP : Module(
    name = "StorageESP",
    description = "Draws an ESP on top of storage units",
    category = Category.RENDER
) {
    private val page by setting("Page", Page.TYPE)

    /* Type settings */
    private val chest by setting("Chest", true, { page == Page.TYPE })
    private val shulker by setting("Shulker", true, { page == Page.TYPE })
    private val enderChest by setting("Ender Chest", true, { page == Page.TYPE })
    private val frame by setting("Item Frame", true, { page == Page.TYPE })
    private val withShulkerOnly by setting("With Shulker Only", true, { page == Page.TYPE && frame })
    private val furnace by setting("Furnace", false, { page == Page.TYPE })
    private val dispenser by setting("Dispenser", false, { page == Page.TYPE })
    private val hopper by setting("Hopper", false, { page == Page.TYPE })
    private val cart by setting("Minecart", false, { page == Page.TYPE })

    /* Color settings */
    private val colorChest by setting("Chest Color", DyeColors.ORANGE, { page == Page.COLOR })
    private val colorDispenser by setting("Dispenser Color", DyeColors.LIGHT_GRAY, { page == Page.COLOR })
    private val colorShulker by setting("Shulker Color", DyeColors.MAGENTA, { page == Page.COLOR })
    private val colorEnderChest by setting("Ender Chest Color", DyeColors.PURPLE, { page == Page.COLOR })
    private val colorFurnace by setting("Furnace Color", DyeColors.LIGHT_GRAY, { page == Page.COLOR })
    private val colorHopper by setting("Hopper Color", DyeColors.GRAY, { page == Page.COLOR })
    private val colorCart by setting("Cart Color", DyeColors.GREEN, { page == Page.COLOR })
    private val colorFrame by setting("Frame Color", DyeColors.ORANGE, { page == Page.COLOR })

    /* Tracer settings */
    private val tracerChest by setting("Chest Tracers", true, { page == Page.TRACERS})
    private val tracerDispenser by setting("Dispenser Tracers", true, { page == Page.TRACERS})
    private val tracerShulker by setting("Shulker Tracers", true, { page == Page.TRACERS})
    private val tracerEnderChest by setting("Ender Chest Tracers", true, { page == Page.TRACERS})
    private val tracerFurnace by setting("Furnace Tracers", true, { page == Page.TRACERS})
    private val tracerHopper by setting("Hopper Tracers", true, { page == Page.TRACERS})
    private val tracerCart by setting("Cart Tracers", true, { page == Page.TRACERS})
    private val tracerFrame by setting("Frame Tracers", true, { page == Page.TRACERS})

    /* Render settings */
    private val filled by setting("Filled", true, { page == Page.RENDER })
    private val outline by setting("Outline", true, { page == Page.RENDER })
    private val tracer by setting("Tracer", true, { page == Page.RENDER })
    private val aFilled by setting("Filled Alpha", 31, 0..255, 1, { page == Page.RENDER && filled })
    private val aOutline by setting("Outline Alpha", 127, 0..255, 1, { page == Page.RENDER && outline })
    private val aTracer by setting("Tracer Alpha", 200, 0..255, 1, { page == Page.RENDER && tracer })
    private val thickness by setting("Line Thickness", 2.0f, 0.25f..5.0f, 0.25f, { page == Page.RENDER })

    private enum class Page {
        TYPE, COLOR, TRACERS, RENDER
    }

    override fun getHudInfo(): String {
        return renderer.size.toString()
    }

    private var cycler = HueCycler(600)
    private val renderer = ESPRenderer()

    init {
        listener<RenderWorldEvent> {
            renderer.render(false)
        }

        safeAsyncListener<TickEvent.ClientTickEvent> {
            if (it.phase != TickEvent.Phase.START) return@safeAsyncListener

            cycler++
            val cached = ArrayList<Quad<AxisAlignedBB, ColorHolder, Int, Boolean>>()

            coroutineScope {
                launch(Dispatchers.Default) {
                    updateRenderer()
                }
                launch(Dispatchers.Default) {
                    updateTileEntities(cached)
                }
                launch(Dispatchers.Default) {
                    updateEntities(cached)
                }
            }

            renderer.replaceAll(cached)
        }
    }

    private fun updateRenderer() {
        renderer.aFilled = if (filled) aFilled else 0
        renderer.aOutline = if (outline) aOutline else 0
        renderer.aTracer = if (tracer) aTracer else 0
        renderer.thickness = thickness
    }

    private fun SafeClientEvent.updateTileEntities(list: MutableList<Quad<AxisAlignedBB, ColorHolder, Int, Boolean>>) {
        for (tileEntity in world.loadedTileEntityList.toList()) {
            if (!checkTileEntityType(tileEntity)) continue

            val box = world.getBlockState(tileEntity.pos).getSelectedBoundingBox(world, tileEntity.pos) ?: continue
            val color = getTileEntityColor(tileEntity) ?: continue
            var side = GeometryMasks.Quad.ALL
            var tracer = checkTileEntityTracers(tileEntity)

            if (tileEntity is TileEntityChest) {
                // Leave only the colliding face and then flip the bits (~) to have ALL but that face
                if (tileEntity.adjacentChestZNeg != null) side = (side and GeometryMasks.Quad.NORTH).inv()
                if (tileEntity.adjacentChestXPos != null) side = (side and GeometryMasks.Quad.EAST).inv()
                if (tileEntity.adjacentChestZPos != null) side = (side and GeometryMasks.Quad.SOUTH).inv()
                if (tileEntity.adjacentChestXNeg != null) side = (side and GeometryMasks.Quad.WEST).inv()
            }

            synchronized(list) {
                list.add(Quad(box, color, side, tracer))
            }
        }
    }

    private fun checkTileEntityType(tileEntity: TileEntity) =
        chest && tileEntity is TileEntityChest
            || dispenser && tileEntity is TileEntityDispenser
            || shulker && tileEntity is TileEntityShulkerBox
            || enderChest && tileEntity is TileEntityEnderChest
            || furnace && tileEntity is TileEntityFurnace
            || hopper && tileEntity is TileEntityHopper

    private fun checkTileEntityTracers(tileEntity: TileEntity) =
        tracerChest && tileEntity is TileEntityChest
            || tracerDispenser && tileEntity is TileEntityDispenser
            || tracerShulker && tileEntity is TileEntityShulkerBox
            || tracerEnderChest && tileEntity is TileEntityEnderChest
            || tracerFurnace && tileEntity is TileEntityFurnace
            || tracerHopper && tileEntity is TileEntityHopper


    private fun getTileEntityColor(tileEntity: TileEntity): ColorHolder? {
        val color = when (tileEntity) {
            is TileEntityChest -> colorChest
            is TileEntityDispenser -> colorDispenser
            is TileEntityShulkerBox -> colorShulker
            is TileEntityEnderChest -> colorEnderChest
            is TileEntityFurnace -> colorFurnace
            is TileEntityHopper -> colorHopper
            else -> return null
        }.color
        return if (color == DyeColors.RAINBOW.color) {
            cycler.currentRgb()
        } else color
    }

    private fun SafeClientEvent.updateEntities(list: MutableList<Quad<AxisAlignedBB, ColorHolder, Int, Boolean>>) {
        for (entity in world.loadedEntityList.toList()) {
            if (!checkEntityType(entity)) continue

            val box = entity.renderBoundingBox ?: continue
            val color = getEntityColor(entity) ?: continue
            val tracer = checkEntityTracers(entity)

            synchronized(list) {
                list.add(Quad(box, color, GeometryMasks.Quad.ALL, tracer))
            }
        }
    }

    private fun checkEntityType(entity: Entity) =
        entity is EntityItemFrame && frame && (!withShulkerOnly || entity.displayedItem.item is ItemShulkerBox)
            || (entity is EntityMinecartChest || entity is EntityMinecartHopper || entity is EntityMinecartFurnace) && cart

    private fun checkEntityTracers(entity: Entity) =
        entity is EntityItemFrame && tracerFrame
            || (entity is EntityMinecartChest || entity is EntityMinecartHopper || entity is EntityMinecartFurnace) && tracerCart

    private fun getEntityColor(entity: Entity): ColorHolder? {
        val color = when (entity) {
            is EntityMinecartContainer -> colorCart
            is EntityItemFrame -> colorFrame
            else -> return null
        }.color
        return if (color == DyeColors.RAINBOW.color) {
            cycler.currentRgb()
        } else color
    }

}
