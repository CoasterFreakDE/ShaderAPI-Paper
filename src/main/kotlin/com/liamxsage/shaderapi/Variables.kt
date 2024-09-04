package com.liamxsage.shaderapi

import net.minecraft.resources.ResourceLocation

const val SHADERAPI_REQUEST_CHANNEL = "shaderapi:request_shader_url"
const val SHADERAPI_STATUS_CHANNEL = "shaderapi:status_response"
const val SHADERAPI_SEND_DATA_CHANNEL = "shaderapi:receive_shader_url"
val SHADERAPI_RESOURCELOCATION = ResourceLocation.parse(SHADERAPI_SEND_DATA_CHANNEL)