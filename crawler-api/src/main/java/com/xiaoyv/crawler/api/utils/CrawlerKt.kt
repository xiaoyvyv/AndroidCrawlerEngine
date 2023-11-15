package com.xiaoyv.crawler.api.utils

import com.xiaoyv.crawler.common.entity.CrawlerInfo
import com.xiaoyv.crawler.common.entity.CrawlerManifest

/**
 * CrawlerKt
 *
 * @author why
 * @since 11/14/23
 */
fun CrawlerManifest.getCrawler(crawlerName: String): CrawlerInfo {
    return requireNotNull(crawlers.find { it.name == crawlerName }) {
        "CrawlerName: $crawlerName not found!"
    }
}