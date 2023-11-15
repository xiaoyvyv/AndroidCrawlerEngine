# AndroidCrawlerEngine

A dynamic crawler plug-in for the Android platform based on Dex dynamic loading, which can dynamically load and execute the dex plug-in package, and can realize real-time updates of crawler and other functions.

## 它能做什么？

动态加载一个爬虫插件包，然后执行爬虫插件包的相关方法，输出结果。一般常用于爬虫程序，需要频繁更新爬虫的相关逻辑等的插件包，而不需要重新更新整个程序，可以看做一个小而美的插件库。

> 一些收罗互联网资源的小说、漫画、视频、文章等爬虫App的逻辑基本上是存放在服务端的，这样容易导致一个问题就是全部由服务端去爬取IP容易被封，可以把这些爬虫爬取网页的逻辑全部写在一个插件包内，打包为一个插件，通过插件版本管理动态更新，就不需要每次爬取的目标网站解析逻辑发生变化而直接更新App了，同时爬虫是在客户端运行的，能减少服务端的压力，也规避的服务端IP容易被封的问题。

当然热门的热修复框架或App插件框架也能做到这种，但是都太复杂了，我的需求是仅能动态执行些编译好的逻辑代码，输出想要的内容且能动态下发更新，插件包不需要UI，所以就写了这个小而美的爬虫引擎。

## 怎么引入？

你的项目想获取这个动态加载爬虫插件包的能力很简单，仅添加一行依赖就能接入了。

```kotlin
implementation(project(":crawler-core"))
```

## 怎么使用？
1. 加载一个爬虫包（xxx.pak），一个爬虫包内有一个清单文件和多个爬虫类，每个爬虫类还可以有多个方法。
   
   ```kotlin
   // 爬虫包文件可读路径
   val crawlerPakPath = "/xx/xx/xx.pak"
   // 加载爬虫包
   val handle = CrawlerEngine.loadCrawlerPak(crawlerPakPath)
   ```
3. 获取爬虫包内的某个爬虫类
   
   ```kotlin
   // 获取名为 `webCralwer` 的爬虫类
   val webCralwer = handle.getCrawlerByName("webCralwer")
   ```
5. 执行爬虫类的某个方法
   
   ```kotlin
   /**
    * 调用指定的 [CrawlerMethod] 方法
    *
    * @param functionName 清单配置的方法名称
    * @param functionParamValues 方法的入参，类型需要和清单配置的方法参数的 Types 匹配，否则无法调用
    * @param functionAccept 根据名称匹配到多个时，选择需要调用的那一个
    * @param callback 方法执行完成返回的内容，Void 时会返回 null
    */
   fun call(
       functionName: String,
       functionParamValues: List<Any?>,
       functionAccept: (List<CrawlerFunction>) -> CrawlerFunction? = { it.firstOrNull() },
       callback: (Any?) -> Unit
   )

   // 示例
   webCralwer.call("httpTest", listOf("https://www.baidu.com")) { result ->
       // 打印方法返回的结果
       Log.i("CrawlerResult",result.toString())
   }
   ```
   是不是很简单？

## 关于爬虫插件包？

爬虫插件包结果也很简单

- 一个 `manifest.json` 清单文件

  清单文件记录了爬虫逻辑文件内的爬虫相关的类列表，和爬虫类相关的可执行方法以及方法的参数类型等。
  这些都是爬虫插件包开发自动生成的，使用时通过 `handle.manifest` 直接获取到这个清单文件模型，然后 `getCrawlerByName` `call` 等方法的爬虫类名，以及调用的方法名等都要从清单文件读取。
  结构如下：
  
  ```json
  {
     "packageId": "com.xiaoyv.crawler.test",
     "name": "plugin.pak",
     "description": "This is a collection of crawler packages",
     "author": "why",
     "versionCode": 1,
     "versionName": "1.0",
     "createTime": 1700030526874,
     "updateTime": 1700030526874,
     "crawlers": [
       {
         "name": "webCrawler",
         "className": "com.xiaoyv.crawler.test.WebTestCrawler",
         "description": "WebTestCrawler",
         "version": 1,
         "function": [
           {
             "name": "httpTest",
             "description": "http test",
             "paramType": [
               "java.lang.String"
             ]
           }
         ]
       },
       {
         "name": "otherCrawler",
         "className": "com.xiaoyv.crawler.test.OtherCrawler",
         "description": "OtherCrawler",
         "version": 1,
         "function": [
           {
             "name": "otherTest",
             "description": "xxx",
             "paramType": [
               "java.lang.String"
             ]
           }
         ]
       }
     ]
  }
  ```
  
- 一个 `classes.dex` 爬虫逻辑的 `dex` 文件

  见爬虫插件开发部分

## 如何开发一个爬虫插件包？
开发一个爬虫插件包也很简单，引入一个 `Gradle plugin` 即可

1. 在项目级的 `build.gradle.kts` 内添加依赖
   
   ```kotlin
   // ...
   
   buildscript {
     dependencies {
       // ...
   
       classpath("com.xiaoyv.gradle:crawler-plugin:$version")
     }
   }
   
   // ...
   ```
2. 新建一个 `application` 类型的空模块，注意是 `application` 不是 `library`。然后引入该爬虫 Gradle 插件和依赖
   
   ```kotlin
   plugins {
       alias(libs.plugins.androidApplication)
       alias(libs.plugins.kotlinAndroid)
   }

   // 引入插件
   apply(plugin = "com.xiaoyv.gradle.crawler")

   // 这里可以配置清单文件的相关开发信息
   configure<com.xiaoyv.gradle.crawler.extension.CrawlerExtension> {
       crawlerName.set("plugin.pak")
       crawlerAuthor.set("why")
       crawlerDescription.set("This is a collection of crawler packages")
       crawlerCreateTime.set(System.currentTimeMillis())
       crawlerUpdateTime.set(System.currentTimeMillis())
   }

   dependencies {
       // 注意这里使用的是 compileOnly，因为加载爬虫插件包的项目已经存在相关的类了，不需要插件包引入。
       compileOnly(project(":crawler-api"))

       // ...
   }
   ```

3. 在这个模块创建一个爬虫类，如：`WebTestCrawler` 继承自 `ICrawler` 类。

   注意：

   爬虫类需要使用注解 `CrawlerObj` 标记，`CrawlerObj` 可以配置爬虫类的名字描述等，会自动生成到清单文件。

   爬虫类里面暴露的爬虫方法必须要使用 `CrawlerMethod` 标记，`CrawlerMethod` 可以配置描述信息。

   如果爬虫类没有被 `CrawlerObj` 标记，将不会生成到清单文件，也无法被 `getCrawlerByName` 加载。

   如果爬虫类的需要暴露的方法没有被 `CrawlerMethod` 标记，则该方法不会生成到清单文件，也无法通过 `call` 等方法调用。

   ```kotlin
   package com.xiaoyv.crawler.test
   
   import android.content.Context
   import android.widget.Toast
   import com.xiaoyv.crawler.annotation.CrawlerMethod
   import com.xiaoyv.crawler.annotation.CrawlerObj
   import com.xiaoyv.crawler.api.ICrawler
   import okhttp3.HttpUrl.Companion.toHttpUrl
   import okhttp3.Request
   import org.jsoup.Jsoup
   
   @CrawlerObj(name = "webCrawler", description = "WebTestCrawler", version = 1)
   class WebTestCrawler : ICrawler() {
   
       override fun onCreate() {
   
       }
   
       @CrawlerMethod(description = "http test")
       fun httpTest(url: String): String {
           val response = useHttpClient.newCall(Request(url.toHttpUrl())).execute()
           return response.body.string()
       }
   }
   ```
4. 编译插件包

   直接执行该模块的 `gradle` 任务 `build` 组下的 `buildCrawlerRelease` 任务即可，生成的插件包位于 `build/outputs` 目录下面。
   
   ![图片](https://github.com/xiaoyvyv/AndroidCrawlerEngine/assets/29088158/eda7a0aa-525e-4edb-a335-f1d869ca4097)
   
   生成的插件包即可通过 `CrawlerEngine` 进行加载运行。
