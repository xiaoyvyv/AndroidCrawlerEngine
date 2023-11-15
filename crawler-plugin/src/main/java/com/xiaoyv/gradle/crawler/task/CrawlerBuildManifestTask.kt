package com.xiaoyv.gradle.crawler.task

import com.google.gson.Gson
import com.xiaoyv.crawler.common.entity.CrawlerManifest
import com.xiaoyv.gradle.crawler.CrawlerClassVisitor
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.objectweb.asm.ClassReader

abstract class CrawlerBuildManifestTask : DefaultTask() {
    @get:Input
    abstract val configInfo: Property<CrawlerManifest>

    @get:InputFiles
    abstract val allDirectories: ListProperty<Directory>

    @get:InputFiles
    abstract val allJars: ListProperty<RegularFile>

    @get:OutputFile
    abstract val output: RegularFileProperty

    private val gson by lazy {
        Gson().newBuilder()
            .setPrettyPrinting()
            .create()
    }

    @TaskAction
    fun taskAction() {
        val crawlerManifest = configInfo.getOrElse(CrawlerManifest())
        allDirectories.get().forEach { directory ->
            directory.asFile.walk()
                .filter { it.isFile }
                .forEach { file ->
                    if (file.name.endsWith(".class")) {
                        val classVisitor = CrawlerClassVisitor(crawlerManifest)

                        file.inputStream().use {
                            ClassReader(it).accept(classVisitor, 0)
                        }
                    }
                }
        }

        output.get().asFile.writeText(gson.toJson(crawlerManifest))
    }

    override fun getGroup() = "build"
}