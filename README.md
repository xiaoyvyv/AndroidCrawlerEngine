# AndroidCrawlerEngine
A dynamic crawler plug-in for the Android platform based on Dex dynamic loading, which can dynamically load and execute the dex plug-in package, and can realize real-time updates of crawler and other functions.
## 它能做什么？
动态加载一个插件包，然后执行插件包的相关方法，输出结果。一般常用于爬虫程序，需要频繁更新爬虫的相关逻辑等的插件包，而不需要重新更新整个程序，可以看做一个小而美的插件库。

> 一些收罗互联网资源的小说、漫画、视频、文章等爬虫App的逻辑基本上是存放在服务端的，这样容易导致一个问题就是全部由服务端去爬取IP容易被封，可以把这些爬虫爬取网页的逻辑全部写在一个插件包内，打包为一个插件，通过插件版本管理动态更新，就不需要每次爬取的目标网站解析逻辑发生变化而直接更新App了，同时爬虫是在客户端运行的，能减少服务端的压力，也规避的服务端IP容易被封的问题。

当然热门的热修复框架或App插件框架也能做到这种，但是都太复杂了，我的需求是仅能动态执行些编译好的逻辑代码，输出想要的内容且能动态下发更新，插件包不需要UI，所以就写了这个小而美的爬虫引擎。

## 怎么引入？
你的项目想获取这个动态加载插件包的能力很简单，仅添加一行依赖就能接入了。
```kotlin
implementation(project(":crawler-core"))
```
## 怎么使用？
1. 加载一个爬虫包（xxx.pak），一个爬虫包内可以有多个爬虫类，每个爬虫类还可以有多个方法。
   ```kotlin
   // 爬虫包文件可读路径
   val crawlerPakPath = "/xx/xx/xx.pak"
   // 加载爬虫包
   val handle = CrawlerEngine.loadCrawlerPak(crawlerPakPath)
   ```
2. 获取爬虫包内的某个爬虫类
   ```kotlin
   // 获取名为 `animeCralwer` 的爬虫类
   val animeCralwer = handle.getCrawlerByName("animeCralwer")
   ```
3. 执行爬虫类的某个方法
   ```kotlin
   iCrawler.call("httpTest", listOf("https://www.5axxw.com")) { result ->
       // 打印方法返回的结果
       Log.i("CrawlerResult",result.toString())
   }
   ```
5. 

6. 
7. 
8. 
