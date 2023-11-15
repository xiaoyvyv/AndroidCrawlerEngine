package com.xiaoyv.crawler.core

import com.xiaoyv.crawler.annotation.CrawlerMethod
import com.xiaoyv.crawler.annotation.CrawlerObj
import com.xiaoyv.crawler.api.ICrawler
import com.xiaoyv.crawler.api.ICrawlerCallable
import com.xiaoyv.crawler.api.utils.getCrawler
import com.xiaoyv.crawler.api.utils.getDeclaredMethodCompat
import com.xiaoyv.crawler.api.utils.invokeCompat
import com.xiaoyv.crawler.common.entity.CrawlerManifest
import java.util.concurrent.ConcurrentHashMap

/**
 * CrawlerHandle
 *
 * @author why
 * @since 11/9/23
 */
class CrawlerPakHandle(
    val appClassLoader: ClassLoader,
    val pluginClassLoader: ClassLoader,
    val manifest: CrawlerManifest,
) : ICrawlerHandle, ICrawlerCallable<ICrawler> {
    private val crawlerCache = ConcurrentHashMap<String, ICrawler>()

    override fun clearCache(crawlerName: String) {
        crawlerCache.remove(crawlerName)
    }

    override fun getCrawlerByName(crawlerName: String): ICrawler {
        return synchronized(this) {
            if (crawlerCache.containsKey(crawlerName)) {
                return@synchronized requireNotNull(crawlerCache[crawlerName])
            }

            val crawler = manifest.getCrawler(crawlerName)
            val crawlerClassName = crawler.className
            val targetCrawler = loadClassInstance(crawlerClassName)
            if (targetCrawler !is ICrawler) {
                throw IllegalStateException("Class '$crawlerName' not implement ${ICrawler::class.qualifiedName}!")
            }
            targetCrawler.appClassLoader = appClassLoader
            targetCrawler.pluginClassLoader = pluginClassLoader
            targetCrawler.context = CrawlerEngine.context
            targetCrawler.crawlerInfo = crawler
            targetCrawler.globalConfig = CrawlerEngine.config
            targetCrawler.callDelegate = this
            targetCrawler.onCreate()
            crawlerCache[crawlerName] = targetCrawler
            return@synchronized targetCrawler
        }
    }

    override fun loadClassInstance(className: String): Any {
        val clazz = requireNotNull(pluginClassLoader.loadClass(className))
        if (clazz.isAnnotationPresent(CrawlerObj::class.java).not()) {
            throw IllegalStateException("The annotation ${CrawlerObj::class.simpleName} of $className does not exist!")
        }

        val constructor = clazz.getConstructor().apply {
            isAccessible = true
        }
        return requireNotNull(constructor.newInstance())
    }

    override fun call(
        instance: ICrawler,
        functionName: String,
        paramTypes: List<Class<*>>,
        paramValues: List<Any?>,
        callback: (Any?) -> Unit
    ) {
        val targetMethod = instance.javaClass.getDeclaredMethodCompat(functionName, paramTypes)
        val method = targetMethod.first.apply { isAccessible = true }
        if (method.isAnnotationPresent(CrawlerMethod::class.java).not()) {
            throw IllegalStateException("The annotation ${CrawlerMethod::class.simpleName} of method $method(..) does not exist!")
        }
        method.invokeCompat(instance, paramValues.toList(), targetMethod.second, callback)
    }
}