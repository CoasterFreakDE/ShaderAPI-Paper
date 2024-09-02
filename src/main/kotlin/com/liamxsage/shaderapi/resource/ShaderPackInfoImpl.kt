package com.liamxsage.shaderapi.resource

import net.kyori.adventure.internal.Internals
import net.kyori.examination.ExaminableProperty
import org.jetbrains.annotations.ApiStatus
import java.io.IOException
import java.net.URI
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.stream.Stream

internal class ShaderPackInfoImpl(uri: URI, hash: String) : ShaderPackInfo {
    private val uri: URI
    private val hash: String

    init {
        this.uri = Objects.requireNonNull(uri, "uri")
        this.hash = Objects.requireNonNull(hash, "hash")
    }

    override fun uri(): URI {
        return this.uri
    }

    override fun hash(): String {
        return this.hash
    }

    override fun examinableProperties(): Stream<out ExaminableProperty> {
        return Stream.of(ExaminableProperty.of("uri", this.uri), ExaminableProperty.of("hash", this.hash))
    }

    @ApiStatus.Internal
    override fun toString(): String {
        return Internals.toString(this)
    }

    internal class BuilderImpl : ShaderPackInfo.Builder {
        private var id: UUID? = null
        private var uri: URI? = null
        private var hash: String? = null

        override fun uri(uri: URI): ShaderPackInfo.Builder {
            this.uri = Objects.requireNonNull(uri, "uri")
            if (this.id == null) {
                this.id = UUID.nameUUIDFromBytes(uri.toString().toByteArray(StandardCharsets.UTF_8))
            }

            return this
        }

        override fun hash(hash: String): ShaderPackInfo.Builder {
            this.hash = Objects.requireNonNull(hash, "hash")
            return this
        }

        override fun build(): ShaderPackInfo {
            return ShaderPackInfoImpl(uri!!, hash!!)
        }

        override fun computeHashAndBuild(executor: Executor): CompletableFuture<ShaderPackInfo> {
            return computeHash(uri!!, executor).thenApply { hash ->
                checkNotNull(hash) { "Failed to compute hash for $uri" }
                this.hash(hash)
                this.build()
            }
        }
    }

    companion object {
        fun computeHash(uri: URI, exec: Executor): CompletableFuture<String?> {
            val result: CompletableFuture<String?> = CompletableFuture<String?>()
            exec.execute {
                try {
                    val url = uri.toURL()
                    val conn = url.openConnection()
                    conn.addRequestProperty(
                        "User-Agent",
                        "adventure/" + ShaderPackInfoImpl::class.java.getPackage().specificationVersion + " (pack-fetcher)"
                    )
                    val inputStream = conn.getInputStream()

                    try {
                        val digest = MessageDigest.getInstance("SHA-1")
                        val buf = ByteArray(8192)

                        while (true) {
                            var read: Int
                            if ((inputStream!!.read(buf).also { read = it }) == -1) {
                                result.complete(bytesToString(digest.digest()))
                                break
                            }

                            digest.update(buf, 0, read)
                        }
                    } catch (var9: Throwable) {
                        if (inputStream != null) {
                            try {
                                inputStream.close()
                            } catch (var8: Throwable) {
                                var9.addSuppressed(var8)
                            }
                        }

                        throw var9
                    }

                    inputStream.close()
                } catch (var10: NoSuchAlgorithmException) {
                    val ex: Exception = var10
                    result.completeExceptionally(ex)
                } catch (var10: IOException) {
                    val ex: Exception = var10
                    result.completeExceptionally(ex)
                }
            }
            return result
        }

        private fun bytesToString(arr: ByteArray): String {
            val builder = StringBuilder(arr.size * 2)
            val fmt = Formatter(builder, Locale.ROOT)

            for (b in arr) {
                fmt.format("%02x", b.toInt() and 255)
            }

            return builder.toString()
        }
    }
}