package com.xiaoyv.crawler

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.xiaoyv.crawler.core.CrawlerEngine
import com.xiaoyv.crawler.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val testApk by lazy {
        File(filesDir, "plugin.pak")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener {
            test()
        }
    }

    private fun test() {
        assets.open("plugin.pak").use {
            it.copyTo(FileOutputStream(testApk))
        }

        runCatching {
            val handle = CrawlerEngine.loadCrawlerPak(testApk)
            val iCrawler = handle.getCrawlerByName("com.crawler.xxx")

          thread {
              iCrawler.call("jsoup", listOf("https://www.5axxw.com")) {
                 runOnUiThread {
                     Toast.makeText(this, "Result: $it", Toast.LENGTH_SHORT).show()
                 }
              }
          }

//
//
//            Log.e("Plugin", "Init success!")
//
//            crawlerHandle.call(
//                "com.xiaoyv.crawler.test.WebTestCrawler",
//                "toast",
//                listOf(Context::class.java to this, String::class.java to "hello?")
//            ) {
//                Log.e("Plugin", "call result -> $it")
//            }
        }.onFailure {
            it.printStackTrace()

            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
    }
}