package com.xiaoyv.crawler.api

import com.xiaoyv.crawler.annotation.CrawlerMethod

/**
 * ICrawlerCallable
 *
 * @author why
 * @since 11/14/23
 */
interface ICrawlerCallable<T : Any> {

    /**
     * 调用指定的 [CrawlerMethod] 方法
     *
     * @param instance 目标实例
     * @param functionName 方法名称
     * @param paramValues 方法的入参，类型需要和清单配置的方法参数的 Types 匹配，否则无法调用
     * @param paramTypes 方法的入参 Types
     * @param callback 方法执行完成返回的内容，Void 时会返回 null
     */
    fun call(
        instance: T,
        functionName: String,
        paramTypes: List<Class<*>>,
        paramValues: List<Any?>,
        callback: (Any?) -> Unit
    )
}