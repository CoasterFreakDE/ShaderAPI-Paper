package com.liamxsage.shaderapi.resource

import net.kyori.adventure.builder.AbstractBuilder
import net.kyori.examination.Examinable
import org.jetbrains.annotations.Contract
import java.net.URI
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.ForkJoinPool

interface ShaderPackInfo : Examinable {
    fun uri(): URI

    fun hash(): String

    interface Builder : AbstractBuilder<ShaderPackInfo> {
        @Contract("_ -> this")
        fun uri(uri: URI): Builder

        @Contract("_ -> this")
        fun hash(hash: String): Builder

        override fun build(): ShaderPackInfo

        fun computeHashAndBuild(): CompletableFuture<ShaderPackInfo> {
            return this.computeHashAndBuild(ForkJoinPool.commonPool())
        }

        fun computeHashAndBuild(executor: Executor): CompletableFuture<ShaderPackInfo>
    }

    companion object {
        fun shaderPackInfo(uri: URI, hash: String): ShaderPackInfo {
            return ShaderPackInfoImpl(uri, hash)
        }

        fun shaderPackInfo(): Builder {
            return ShaderPackInfoImpl.BuilderImpl()
        }
    }
}