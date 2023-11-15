package com.xiaoyv.crawler.api

import com.google.gson.Gson
import okhttp3.OkHttpClient

/**
 * CrawlerConfig
 *
 * @author why
 * @since 11/10/23
 */
interface ICrawlerConfig {

    /**
     * 返回 OkHttpClient 的引用单例，会多次调用，请不要直接新建一个实例。
     */
    fun useHttpClient(): OkHttpClient

    /**
     * 返回 Gson 的引用单例，会多次调用，请不要直接新建一个实例。
     */
    fun useGson(): Gson
}