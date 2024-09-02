package com.liamxsage.shaderapi.listeners

import com.liamxsage.klassicx.extensions.getLogger
import com.liamxsage.shaderapi.SHADERAPI_INCOMING_CHANNEL
import com.liamxsage.shaderapi.SHADERAPI_RESOURCELOCATION
import com.liamxsage.shaderapi.resource.ShaderPackInfo
import com.liamxsage.shaderapi.resource.ShaderPackInfoImpl
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



    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray?) {
        if (SHADERAPI_INCOMING_CHANNEL != channel) return  // Ensure it's the correct channel

        also {
            if (message == null) return@also
            if (message.isEmpty()) return@also
            val shaderUrl = message.toString(Charsets.UTF_8)
            getLogger().info("Shader URL received: $shaderUrl")
        }

        val shaderUrl = "https://cdn.modrinth.com/data/HVnmMxH1/versions/pAOQ9Amz/ComplementaryReimagined_r5.2.2.zip"

        ShaderPackInfo.shaderPackInfo()
            .uri(URI.create(shaderUrl))
            .computeHashAndBuild().thenApply {
                getLogger().info("Shader hash: ${it.hash()}")
                sendShaderUrl(player, shaderUrl, it.hash())
                getLogger().info("Shader URL sent")
            }
    }

    private fun sendShaderUrl(player: Player, shaderUrl: String, hash: String) {
        try {
            // Create a ByteBuf to hold the data
            val byteBuf = FriendlyByteBuf(Unpooled.buffer())

            // Write the shader URL to the buffer as UTF-8
            byteBuf.writeUtf(shaderUrl)
            byteBuf.writeUtf(hash)
            byteBuf.writeUtf("global")


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