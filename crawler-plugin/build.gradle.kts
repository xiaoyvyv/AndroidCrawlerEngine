import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java-library")
    id("maven-publish")
    kotlin("jvm")
}

group = "com.xiaoyv.gradle.crawler"
version = "0.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(gradleApi())
    implementation(gradleKotlinDsl())
    implementation(kotlin("stdlib"))

    implementation("com.android.tools.build:gradle:8.3.0-alpha13")
    implementation("com.android.tools.build:gradle-api:8.3.0-alpha13")
    implementation("com.google.code.gson:gson:2.10.1")

    implementation("org.ow2.asm:asm:9.5")
    implementation("org.ow2.asm:asm-commons:9.5")
    implementation("org.ow2.asm:asm-util:9.5")

    implementation(project(":crawler-annotation"))
    implementation(project(":crawler-common"))
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.apiVersion = "1.8"
    kotlinOptions.languageVersion = "1.8"
    kotlinOptions.jvmTarget = "11"
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                groupId = "com.xiaoyv.gradle"
                artifactId = "crawler-plugin"
                version = "0.0.1"

                from(components["kotlin"])
            }
        }
    }
}