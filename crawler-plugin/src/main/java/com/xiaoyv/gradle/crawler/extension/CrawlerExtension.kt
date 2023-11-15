package com.xiaoyv.gradle.crawler.extension

import org.gradle.api.provider.Property

/**
 * CrawlerExtension
 *
 * @author why
 * @since 11/15/23
 */
interface CrawlerExtension {
    val crawlerName: Property<String>
    val crawlerDescription: Property<String>
    val crawlerAuthor: Property<String>
    val crawlerCreateTime: Property<Long>
    val crawlerUpdateTime: Property<Long>
}