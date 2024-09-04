package com.liamxsage.shaderapi

import com.liamxsage.shaderapi.listeners.ShaderAPIListener
import com.liamxsage.shaderapi.listeners.ShaderStatusChangeListener
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.messaging.Messenger
import java.util.*
import kotlin.system.measureTimeMillis

class ShaderAPI : JavaPlugin() {

    companion object {
        lateinit var instance: ShaderAPI
            private set
    }

    init {
        instance = this
    }

    lateinit var serverGroup: String

    override fun onEnable() {
        saveDefaultConfig()

        serverGroup = config.getString("serverGroup") ?: ""
        if (serverGroup.isEmpty() || serverGroup == "null") {
            serverGroup = UUID.randomUUID().toString()
            config["serverGroup"] = serverGroup
            saveConfig()
        }

        // Plugin startup logic
        val time = measureTimeMillis {
            val messenger: Messenger = Bukkit.getMessenger()
            val listener = ShaderAPIListener()
            messenger.registerIncomingPluginChannel(this, SHADERAPI_REQUEST_CHANNEL, listener)
            messenger.registerIncomingPluginChannel(this, SHADERAPI_STATUS_CHANNEL, listener)
            messenger.registerOutgoingPluginChannel(this, SHADERAPI_SEND_DATA_CHANNEL)

            Bukkit.getPluginManager().registerEvents(ShaderStatusChangeListener(), this)
        }
        println("Plugin enabled in $time ms")
    }
}