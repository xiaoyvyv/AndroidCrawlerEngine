package com.xiaoyv.crawler.annotation

/**
 * CrawlerObj
 *
 * @author why
 * @since 11/13/23
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class CrawlerObj(
    val name: String = "",
    val description: String = "",
    val version: Int = 1,
)
