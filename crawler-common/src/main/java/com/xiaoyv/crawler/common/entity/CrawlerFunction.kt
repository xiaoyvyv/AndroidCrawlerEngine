package com.xiaoyv.crawler.common.entity

import java.io.Serializable

data class CrawlerFunction(
    var name: String = "",
    var description: String = "",
    var paramType: ArrayList<String> = arrayListOf(),
) : Serializable