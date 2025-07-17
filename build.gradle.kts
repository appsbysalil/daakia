plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
    id("org.jetbrains.intellij") version "1.17.2"
}

group = "com.salilvnair.jb.plugin"
version = "2.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework:spring-web:6.1.5")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.3.1") {
        exclude("org.slf4j", "slf4j-api")
    }
    implementation("com.fifesoft:rsyntaxtextarea:3.4.0")
    implementation("org.json:json:20240303")
    implementation("org.xerial:sqlite-jdbc:3.45.3.0")
    compileOnly("org.projectlombok:lombok:0.11.0")
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2024.3.2")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf(/* Plugin Dependencies */))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("243")
        untilBuild.set("253.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
