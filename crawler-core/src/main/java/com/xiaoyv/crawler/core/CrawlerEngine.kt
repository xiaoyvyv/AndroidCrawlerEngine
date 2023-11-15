package com.xiaoyv.crawler.core

import android.content.Context
import com.google.gson.Gson
import com.xiaoyv.crawler.api.ICrawlerConfig
import com.xiaoyv.crawler.common.entity.CrawlerManifest
import dalvik.system.DexClassLoader
import okhttp3.OkHttpClient
import java.io.File
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.zip.ZipFile

/**
 * CrawlerEngine
 *
 * @author why
 * @since 11/9/23
 */
object CrawlerEngine {
    internal lateinit var context: WeakReference<Context>

    private val dexMap = ConcurrentHashMap<String, CrawlerPakHandle>()

    private val optimizedDir: String?
        get() {
            val cxt = context.get() ?: return null

            return File(cxt.filesDir, "crawler").let {
                it.mkdirs()
                it.absolutePath
            }
        }

    /**
     * 配置全局的 Config
     */
    var config: ICrawlerConfig

    /**
     * 默认的 OkHttpClient
     */
    private val defaultOkHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .writeTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    /**
     * 默认的 Gson
     */
    private val defaultGson: Gson by lazy { Gson().newBuilder().create() }

    init {
        config = object : ICrawlerConfig {
            override fun useHttpClient() = defaultOkHttpClient

            override fun useGson() = defaultGson
        }
    }

    /**
     * 加载一个插件包
     *
     * @param crawlerPakFile 插件包可读路径
     */
    @Throws(Exception::class)
    fun loadCrawlerPak(crawlerPakFile: File): CrawlerPakHandle {
        return synchronized(this) {
            val cxt = requireNotNull(context.get())
            val crawlerPath = crawlerPakFile.absolutePath

            if (dexMap.containsKey(crawlerPath)) {
                return@synchronized requireNotNull(dexMap[crawlerPath])
            }

            val pluginClassLoader = DexClassLoader(crawlerPath, optimizedDir, null, cxt.classLoader)

            val manifest = runCatching {
                ZipFile(crawlerPakFile).use {
                    it.getInputStream(it.getEntry("manifest.json")).use { stream ->
                        config.useGson().fromJson(
                            stream.readBytes().decodeToString(),
                            CrawlerManifest::class.java
                        )
                    }
                }
            }.getOrElse {
                throw IllegalStateException("Manifest parse error!", it)
            }

            val handle = CrawlerPakHandle(
                appClassLoader = cxt.classLoader,
                pluginClassLoader = pluginClassLoader,
                manifest = manifest,
            )

            dexMap[crawlerPath] = handle
            return@synchronized handle
        }
    }
}