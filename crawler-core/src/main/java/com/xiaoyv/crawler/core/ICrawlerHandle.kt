package com.xiaoyv.crawler.core

import com.xiaoyv.crawler.api.ICrawler

/**
 * IHandle
 *
 * @author why
 * @since 11/9/23
 */
interface ICrawlerHandle {

    fun loadClassInstance(className: String): Any

    fun getCrawlerByName(crawlerName: String): ICrawler

    fun clearCache(crawlerName: String)
}