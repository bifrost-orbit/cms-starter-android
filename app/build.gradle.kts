import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

val local = Properties().apply {
    rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use(::load)
}

fun setting(name: String, default: String = ""): String = "\"${local.getProperty(name) ?: default}\""

android {
    namespace = "com.northwilds.app"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.northwilds.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "CMS_API_URL", setting("cms.apiUrl", "https://api.bifrostorbit.com/cms"))
        buildConfigField("String", "CMS_PUBLIC_KEY", setting("cms.publicKey"))
        buildConfigField("String", "CMS_REGION", setting("cms.region", "global"))
        buildConfigField("String", "DRAFTS_TOKEN", "\"\"")
    }

    buildTypes {
        create("drafts") {
            initWith(getByName("debug"))
            applicationIdSuffix = ".drafts"
            buildConfigField("String", "CMS_API_URL", setting("drafts.apiUrl"))
            buildConfigField("String", "DRAFTS_TOKEN", setting("drafts.token"))
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation("com.bifrostorbit:cms-sdk:0.2.0")

    implementation(platform("androidx.compose:compose-bom:2026.09.00"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.11.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.11.0")
    implementation("androidx.navigation:navigation-compose:2.10.2")
    implementation("io.coil-kt.coil3:coil-compose:3.6.3")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.6.3")
}
