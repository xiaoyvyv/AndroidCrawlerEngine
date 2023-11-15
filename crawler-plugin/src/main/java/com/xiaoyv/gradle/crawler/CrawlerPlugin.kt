@file:Suppress("UnstableApiUsage")

package com.xiaoyv.gradle.crawler

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import com.android.build.gradle.internal.tasks.DexMergingTask
import com.xiaoyv.crawler.common.entity.CrawlerManifest
import com.xiaoyv.gradle.crawler.extension.CrawlerExtension
import com.xiaoyv.gradle.crawler.task.CrawlerBuildManifestTask
import org.gradle.api.NonNullApi
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Zip
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import java.util.Locale


@NonNullApi
class CrawlerPlugin : Plugin<Project> {
    private val androidApplicationPluginId = "com.android.application"

    private fun String.capitalize(): String {
        return replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
    }

    override fun apply(project: Project) {
        val message = "Module (:${project.name}) is not apply plugin: $androidApplicationPluginId"
        if (project.plugins.hasPlugin(androidApplicationPluginId).not()) {
            project.logger.log(LogLevel.WARN, message)
            return
        }

        project.plugins.withId(androidApplicationPluginId) {
            configureAppPlugin(project)
        }
    }

    private fun buildManifestTaskName(variantName: String): String {
        return "buildCrawlerManifest$variantName"
    }

    private fun configureAppPlugin(project: Project) {
        val androidComponents =
            project.extensions.getByType<ApplicationAndroidComponentsExtension>()

        val buildOutputDir = project.layout.buildDirectory.dir("outputs").get()
        val buildManifestJson = buildOutputDir.dir("manifest").file("manifest.json")
        val variantTasks = arrayListOf<String>()

        val crawlerExtension = project.extensions.create<CrawlerExtension>("crawler")
        var packageId = ""
        var crawlerVersionName = ""
        var crawlerVersionCode = 1

        androidComponents.finalizeDsl {
            it.defaultConfig {
                packageId = this.applicationId.orEmpty()
                crawlerVersionCode = this.versionCode ?: 1
                crawlerVersionName = this.versionName.orEmpty()
            }
        }

        androidComponents.onVariants { variant ->
            val variantName = variant.name.capitalize()
            val buildManifestTaskName = buildManifestTaskName(variantName)
            val buildManifestTask: TaskProvider<CrawlerBuildManifestTask> =
                project.tasks.register<CrawlerBuildManifestTask>(buildManifestTaskName) {
                    output.set(buildManifestJson)
                    configInfo.set(
                        CrawlerManifest(
                            packageId = packageId,
                            name = crawlerExtension.crawlerName.getOrElse("Crawler Bundle"),
                            description = crawlerExtension.crawlerDescription.getOrElse("--"),
                            author = crawlerExtension.crawlerAuthor.getOrElse("--"),
                            versionCode = crawlerVersionCode,
                            versionName = crawlerVersionName,
                            createTime = crawlerExtension.crawlerCreateTime.getOrElse(System.currentTimeMillis()),
                            updateTime = crawlerExtension.crawlerUpdateTime.getOrElse(System.currentTimeMillis())
                        )
                    )
                }

            variant.artifacts
                .forScope(ScopedArtifacts.Scope.PROJECT)
                .use(buildManifestTask)
                .toGet(
                    ScopedArtifact.CLASSES,
                    CrawlerBuildManifestTask::allJars,
                    CrawlerBuildManifestTask::allDirectories
                )

            variantTasks.add(variantName)
        }

        project.afterEvaluate {
            val mergeDexTask = project.tasks.withType(DexMergingTask::class.java)
                .firstOrNull { it.name == "mergeDexRelease" }

            if (mergeDexTask != null) {
                variantTasks.forEach {
                    val zip = project.tasks.create<Zip>("buildCrawler$it")
                    zip.group = "build"
                    zip.archiveFileName.set(crawlerExtension.crawlerName.getOrElse("plugin.pak"))
                    zip.destinationDirectory.set(buildOutputDir)
                    zip.from(mergeDexTask.outputDir)
                    zip.from(buildManifestJson)
                    zip.dependsOn(project.tasks.named(buildManifestTaskName(it)))
                    zip.dependsOn(mergeDexTask)
                }
            }
        }
    }
}

