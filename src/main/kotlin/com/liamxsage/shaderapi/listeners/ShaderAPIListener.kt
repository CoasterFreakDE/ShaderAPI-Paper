package com.liamxsage.shaderapi.listeners

import com.liamxsage.shaderapi.SHADERAPI_REQUEST_CHANNEL
import com.liamxsage.shaderapi.SHADERAPI_RESOURCELOCATION
import com.liamxsage.shaderapi.SHADERAPI_STATUS_CHANNEL
import com.liamxsage.shaderapi.ShaderAPI
import com.liamxsage.shaderapi.events.ShaderStatusChangedEvent
import com.liamxsage.shaderapi.resource.ShaderPackInfo
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket
import net.minecraft.network.protocol.common.custom.DiscardedPayload
import net.minecraft.server.level.ServerPlayer
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import java.net.URI


class ShaderAPIListener : PluginMessageListener {


    private val handleIncomingPackets = mapOf(
        SHADERAPI_REQUEST_CHANNEL to this::onRequestShaderUrl,
        SHADERAPI_STATUS_CHANNEL to this::onShaderStatusResponse
    )

    private val shaderPackInfo: ShaderPackInfo = ShaderPackInfo.shaderPackInfo()
        .uri(URI.create(ShaderAPI.instance.config.getString("shaderPack") ?: "https://cdn.modrinth.com/data/HVnmMxH1/versions/pAOQ9Amz/ComplementaryReimagined_r5.2.2.zip"))
        .computeHashAndBuild().get()

    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray?) {
        if (channel !in handleIncomingPackets.keys) return  // Ensure it's the correct channel

        handleIncomingPackets[channel]?.invoke(player, message)
    }

    private fun onRequestShaderUrl(player: Player, message: ByteArray?) {
        sendShaderUrl(player, shaderPackInfo)
    }

    private fun onShaderStatusResponse(player: Player, message: ByteArray?) {
        val statusResponse = message?.let { String(it, Charsets.UTF_8) } ?: return
        ShaderStatusChangedEvent(player, statusResponse).callEvent()
    }

    private fun sendShaderUrl(player: Player, shaderPackInfo: ShaderPackInfo) {
        try {
            // Create a ByteBuf to hold the data
            val byteBuf = FriendlyByteBuf(Unpooled.buffer())

            // Write the shader URL to the buffer as UTF-8
            byteBuf.writeUtf(shaderPackInfo.uri().toString())
            byteBuf.writeUtf(shaderPackInfo.hash())
            byteBuf.writeUtf(ShaderAPI.instance.serverGroup)


            // Send the data to the client using the correct channel
            val serverPlayer = (player as CraftPlayer).handle
            sendCustomPayload(serverPlayer, byteBuf)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sendCustomPayload(serverPlayer: ServerPlayer, data: ByteBuf) {
        serverPlayer.connection.send(ClientboundCustomPayloadPacket(DiscardedPayload(SHADERAPI_RESOURCELOCATION, data)))
    }


}