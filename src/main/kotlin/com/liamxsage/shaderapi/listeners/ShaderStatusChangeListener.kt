package com.liamxsage.shaderapi.listeners

import com.liamxsage.klassicx.extensions.getLogger
import com.liamxsage.shaderapi.events.ShaderStatusChangedEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ShaderStatusChangeListener : Listener {

    @EventHandler
    fun onShaderStatusChangedEvent(event: ShaderStatusChangedEvent): Unit = with(event) {
        getLogger().info("Shader Status Response by ${player.name}: $status")
    }

}