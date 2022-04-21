package calcite.client

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import calcite.client.command.CommandManager
import calcite.client.gui.GuiManager
import calcite.client.manager.ManagerLoader
import calcite.client.module.ModuleManager
import calcite.client.plugin.PluginManager
import calcite.client.util.threads.mainScope

internal object LoaderWrapper {
    private val loaderList = ArrayList<calcite.client.AsyncLoader<*>>()

    init {
        calcite.client.LoaderWrapper.loaderList.add(ModuleManager)
        calcite.client.LoaderWrapper.loaderList.add(CommandManager)
        calcite.client.LoaderWrapper.loaderList.add(ManagerLoader)
        calcite.client.LoaderWrapper.loaderList.add(GuiManager)
        calcite.client.LoaderWrapper.loaderList.add(PluginManager)
    }

    @JvmStatic
    fun preLoadAll() {
        calcite.client.LoaderWrapper.loaderList.forEach { it.preLoad() }
    }

    @JvmStatic
    fun loadAll() {
        runBlocking {
            calcite.client.LoaderWrapper.loaderList.forEach { it.load() }
        }
    }
}

internal interface AsyncLoader<T> {
    var deferred: Deferred<T>?

    fun preLoad() {
        deferred = preLoadAsync()
    }

    private fun preLoadAsync(): Deferred<T> {
        return mainScope.async { preLoad0() }
    }

    suspend fun load() {
        load0((deferred ?: preLoadAsync()).await())
    }

    fun preLoad0(): T
    fun load0(input: T)
}