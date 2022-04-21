package calcite.client.setting.configs

import calcite.client.setting.settings.AbstractSetting
import calcite.commons.interfaces.Nameable

open class NameableConfig<T : Nameable>(
    name: String,
    filePath: String
) : AbstractConfig<T>(name, filePath) {

    override fun addSettingToConfig(owner: T, setting: AbstractSetting<*>) {
        getGroupOrPut(owner.name).addSetting(setting)
    }

    open fun getSettings(nameable: Nameable) = getGroup(nameable.name)?.getSettings() ?: emptyList()

}
