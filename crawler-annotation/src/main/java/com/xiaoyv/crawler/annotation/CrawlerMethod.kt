package com.xiaoyv.crawler.annotation

/**
 * CrawlerMethod
 *
 * @author why
 * @since 11/13/23
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class CrawlerMethod(
    val description: String = ""
)
