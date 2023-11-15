package com.xiaoyv.crawler.common.entity

import java.io.Serializable


/**
 * CrawlerManifest
 *
 * @author why
 * @since 11/10/23
 */
data class CrawlerManifest(
    var packageId: String? = null,
    var name: String? = null,
    var description: String? = null,
    var author: String? = null,
    var versionCode: Int = 1,
    var versionName: String? = null,
    var createTime: Long = 0,
    var updateTime: Long = 0,
    var crawlers: ArrayList<CrawlerInfo> = arrayListOf(),
) : Serializable