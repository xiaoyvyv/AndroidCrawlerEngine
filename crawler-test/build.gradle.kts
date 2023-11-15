plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
}

if (properties["published"].toString().toBoolean()) {
    apply(plugin = "com.xiaoyv.gradle.crawler")

    configure<com.xiaoyv.gradle.crawler.extension.CrawlerExtension> {
        crawlerName.set("plugin.pak")
        crawlerAuthor.set("why")
        crawlerDescription.set("This is a collection of crawler packages")
        crawlerCreateTime.set(System.currentTimeMillis())
        crawlerUpdateTime.set(System.currentTimeMillis())
    }
}

android {
    namespace = "com.xiaoyv.crawler.test"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.xiaoyv.crawler.test"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    compileOnly(project(":crawler-api"))

    implementation("org.jsoup:jsoup:1.16.2")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
