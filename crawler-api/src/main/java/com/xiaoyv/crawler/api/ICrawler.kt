package com.xiaoyv.crawler.api

import android.content.Context
import com.xiaoyv.crawler.annotation.CrawlerMethod
import com.xiaoyv.crawler.api.utils.classFromName
import com.xiaoyv.crawler.common.entity.CrawlerFunction
import com.xiaoyv.crawler.common.entity.CrawlerInfo
import java.lang.ref.WeakReference

/**
 * ICrawler
 *
 * @author why
 * @since 11/9/23
 */
abstract class ICrawler {
    lateinit var appClassLoader: ClassLoader
    lateinit var pluginClassLoader: ClassLoader
    lateinit var context: WeakReference<Context>
    lateinit var crawlerInfo: CrawlerInfo
    lateinit var globalConfig: ICrawlerConfig
    lateinit var callDelegate: ICrawlerCallable<ICrawler>

    /**
     * 全局配置的 Gson
     */
    val useGson get() = globalConfig.useGson()

    /**
     * 全局配置的 HttpClient
     */
    val useHttpClient get() = globalConfig.useHttpClient()

    /**
     * 当 ICrawler 初始化时执行
     */
    abstract fun onCreate()

    /**
     * 调用指定的 [CrawlerMethod] 方法
     *
     * @param functionName 清单配置的方法名称
     * @param functionParamValues 方法的入参，类型需要和清单配置的方法参数的 Types 匹配，否则无法调用
     * @param functionAccept 根据名称匹配到多个时，选择需要调用的那一个
     * @param callback 方法执行完成返回的内容，Void 时会返回 null
     */
    fun call(
        functionName: String,
        functionParamValues: List<Any?>,
        functionAccept: (List<CrawlerFunction>) -> CrawlerFunction? = { it.firstOrNull() },
        callback: (Any?) -> Unit
    ) {
        val target = crawlerInfo.function
            .filter {
                it.name == functionName && it.paramType.size == functionParamValues.size
            }
            .let { functionAccept(it) }
        val function = requireNotNull(target) {
            "Function '$functionName' not found in crawler manifest!"
        }
        val functionParamTypes = function.paramType.map {
            classFromName(it, pluginClassLoader, appClassLoader)
        }
        callDelegate.call(this, functionName, functionParamTypes, functionParamValues, callback)
    }
}