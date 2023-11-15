# AndroidCrawlerEngine
A dynamic crawler plug-in for the Android platform based on Dex dynamic loading, which can dynamically load and execute the dex plug-in package, and can realize real-time updates of crawler and other functions.
## 它能做什么？
动态加载一个插件包，然后执行插件包的相关方法，输出结果。一般常用于爬虫程序，需要频繁更新爬虫的相关逻辑等的插件包，而不需要重新更新整个程序，可以看做一个小而美的插件库。
> 一些收罗互联网资源的小说、漫画、视频、文章等爬虫App的逻辑基本上是存放在服务端的，这样容易导致一个问题就是全部由服务端去爬取IP容易被封，可以把这些爬虫爬取网页的逻辑全部写在一个插件包内，打包为一个插件，通过插件版本管理动态更新，就不需要每次爬取的目标网站解析逻辑发生变化而直接更新App了，同时爬虫是在客户端运行的，能减少服务端的压力，也规避的服务端IP容易被封的问题。
