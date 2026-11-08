plugins {
    kotlin("jvm") version "2.4.21"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-netty:3.6.1")
    implementation("com.bifrostorbit:cms-sdk:0.2.0")
    runtimeOnly("org.slf4j:slf4j-simple:2.0.17")
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("com.northwilds.drafts.MainKt")
}
