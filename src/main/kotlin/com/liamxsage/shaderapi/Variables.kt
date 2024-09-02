package com.liamxsage.shaderapi

import net.minecraft.resources.ResourceLocation

const val SHADERAPI_INCOMING_CHANNEL = "shaderapi:request_shader_url"
const val SHADERAPI_OUTGOING_CHANNEL = "shaderapi:receive_shader_url"
val SHADERAPI_RESOURCELOCATION = ResourceLocation.parse(SHADERAPI_OUTGOING_CHANNEL)