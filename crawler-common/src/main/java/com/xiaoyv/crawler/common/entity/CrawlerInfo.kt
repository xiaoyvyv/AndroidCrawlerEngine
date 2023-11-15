package com.xiaoyv.crawler.common.entity

import java.io.Serializable

data class CrawlerInfo(
    var name: String = "",
    var className: String = "",
    var description: String = "",
    var version: Int = 1,
    var function: ArrayList<CrawlerFunction> = arrayListOf(),
) : Serializable