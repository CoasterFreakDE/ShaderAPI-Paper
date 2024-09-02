package com.liamxsage.shaderapi

import com.liamxsage.shaderapi.listeners.ShaderAPIListener
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.messaging.Messenger
import kotlin.system.measureTimeMillis

class ShaderAPI : JavaPlugin() {

    companion object {
        lateinit var instance: ShaderAPI
            private set
    }

    init {
        instance = this
    }

    override fun onEnable() {
        // Plugin startup logic
        val time = measureTimeMillis {
            val messenger: Messenger = Bukkit.getMessenger()
            messenger.registerIncomingPluginChannel(this, SHADERAPI_INCOMING_CHANNEL, ShaderAPIListener())
            messenger.registerOutgoingPluginChannel(this, SHADERAPI_OUTGOING_CHANNEL)
        }
        println("Plugin enabled in $time ms")
    }
}