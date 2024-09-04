package com.liamxsage.shaderapi.events

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class ShaderStatusChangedEvent(val player: Player, val status: String) : Event() {

    override fun getHandlers(): HandlerList {
        return HANDLERS
    }

    companion object {
        val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERS
        }
    }
}