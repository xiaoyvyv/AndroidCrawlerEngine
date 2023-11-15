import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.jetbrainsKotlinJvm) apply false
}

buildscript {
    dependencies {
        if (properties["published"].toString().toBoolean()) {
            classpath("com.xiaoyv.gradle:crawler-plugin:${properties["publish.version"]}")
        }
    }
}


allprojects {
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}