package com.xiaoyv.crawler.test

import android.content.Context
import android.widget.Toast
import com.xiaoyv.crawler.annotation.CrawlerMethod
import com.xiaoyv.crawler.annotation.CrawlerObj
import com.xiaoyv.crawler.api.ICrawler
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request
import org.jsoup.Jsoup

/**
 * WebTest
 *
 * @author why
 * @since 11/9/23
 */
@CrawlerObj(name = "com.crawler.xxx", description = "Crawler", version = 1)
class WebTestCrawler : ICrawler() {

    override fun onCreate() {

    }

    @JvmOverloads
    @CrawlerMethod(description = "http test")
    fun httpTest(url: String = "https://www.baidu.com"): String {
        val response = useHttpClient.newCall(Request(url.toHttpUrl())).execute()
        return response.body.string()
    }

    @CrawlerMethod(description = "jsoup test")
    fun jsoup(url: String = "https://www.baidu.com"): String {
        val response = useHttpClient.newCall(Request(url.toHttpUrl())).execute()
        val document = Jsoup.parse(response.body.string())
        return document.select("div").text()
    }

    @CrawlerMethod(description = "toast description")
    fun toast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    @CrawlerMethod(description = "sum description")
    fun sum(a: Int, b: Int, c: Int?): Int {

        return a + b + (c ?: 0)
    }
}