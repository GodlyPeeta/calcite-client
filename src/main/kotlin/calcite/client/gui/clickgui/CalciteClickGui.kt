package calcite.client.gui.clickgui

import calcite.client.gui.AbstractCalciteGui
import calcite.client.gui.clickgui.component.ModuleButton
import calcite.client.gui.clickgui.window.ModuleSettingWindow
import calcite.client.gui.rgui.Component
import calcite.client.gui.rgui.windows.ListWindow
import calcite.client.module.AbstractModule
import calcite.client.module.ModuleManager
import calcite.client.module.modules.client.ClickGUI
import calcite.client.util.math.Vec2f
import org.lwjgl.input.Keyboard

object CalciteClickGui : AbstractCalciteGui<ModuleSettingWindow, AbstractModule>() {

    private val moduleWindows = ArrayList<ListWindow>()

    init {
        val allButtons = ModuleManager.modules
            .groupBy { it.category.displayName }
            .mapValues { (_, modules) -> modules.map { ModuleButton(it) } }

        var posX = 0.0f
        var posY = 0.0f
        val screenWidth = mc.displayWidth / ClickGUI.getScaleFactorFloat()

        for ((category, buttons) in allButtons) {
            val window = ListWindow(category, posX, posY, 90.0f, 300.0f, Component.SettingGroup.CLICK_GUI)

            window.children.addAll(buttons.customSort())
            moduleWindows.add(window)
            posX += 90.0f

            if (posX > screenWidth) {
                posX = 0.0f
                posY += 100.0f
            }
        }

        windowList.addAll(moduleWindows)
    }

    override fun onDisplayed() {
        reorderModules()

        super.onDisplayed()
    }

    override fun onGuiClosed() {
        super.onGuiClosed()
        setModuleButtonVisibility { true }
    }

    override fun newSettingWindow(element: AbstractModule, mousePos: Vec2f): ModuleSettingWindow {
        return ModuleSettingWindow(element, mousePos.x, mousePos.y)
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (keyCode == Keyboard.KEY_ESCAPE || keyCode == ClickGUI.bind.value.key && !searching && settingWindow?.listeningChild == null) {
            ClickGUI.disable()
        } else {
            super.keyTyped(typedChar, keyCode)

            val string = typedString.replace(" ", "")

            if (string.isNotEmpty()) {
                setModuleButtonVisibility { moduleButton ->
                    moduleButton.module.name.contains(string, true)
                        || moduleButton.module.alias.any { it.contains(string, true) }
                }
            } else {
                setModuleButtonVisibility { true }
            }
        }
    }

    private fun setModuleButtonVisibility(function: (ModuleButton) -> Boolean) {
        windowList.filterIsInstance<ListWindow>().forEach {
            for (child in it.children) {
                if (child !is ModuleButton) continue
                child.visible = function(child)
            }
        }
    }

    fun reorderModules() {
        val allButtons = ModuleManager.modules
            .groupBy { it.category.displayName }
            .mapValues { (_, modules) -> modules.map { ModuleButton(it) } }

        moduleWindows.forEach { window ->
            window.children.clear()
            allButtons[window.name]?.let { window.children.addAll(it.customSort()) }
        }
    }

    private fun List<ModuleButton>.customSort(): List<ModuleButton> {
        return when (ClickGUI.sortBy.value) {
            ClickGUI.SortByOptions.CUSTOM -> this.sortedByDescending { it.module.priorityForGui.value }
            ClickGUI.SortByOptions.FREQUENCY -> this.sortedByDescending { it.module.clicks.value }
            else -> this.sortedBy { it.name }
        }
    }
}