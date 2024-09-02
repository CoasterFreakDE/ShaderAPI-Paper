package com.liamxsage.shaderapi

import net.minecraft.resources.ResourceLocation

var PREFIX = "<gradient:#f6e58d:#f6e58d>ShaderAPI</gradient> <color:#4a628f>>></color> <color:#b2c2d4>"
val BLOCK_PREFIX =
    "               <color:#4a628f>◆</color> <gradient:#f6e58d:#f6e58d>ShaderAPI</gradient> <color:#4a628f>◆</color>"


const val SHADERAPI_INCOMING_CHANNEL = "shaderapi:request_shader_url"
const val SHADERAPI_OUTGOING_CHANNEL = "shaderapi:receive_shader_url"
val SHADERAPI_RESOURCELOCATION = ResourceLocation.parse(SHADERAPI_OUTGOING_CHANNEL)