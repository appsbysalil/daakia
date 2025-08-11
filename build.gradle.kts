import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform") version "2.6.0"
}

sourceSets {
    main {
        resources.srcDirs("src/main/resources")
    }
}

group = "com.salilvnair.jb.plugin"
version = "3.0.4"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
    maven {
        name = "GraalVM"
        url = uri("https://maven.pkg.jetbrains.space/graalvm/p/graalvm/maven")
    }
}

dependencies {
    intellijPlatform {
        create("IC", "2025.1.1")
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)

        // Add necessary plugin dependencies for compilation here, example:
        // bundledPlugin("com.intellij.java")
        bundledPlugin("com.intellij.modules.json")
    }
    implementation("org.springframework:spring-web:6.2.8")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.3.1") {
        exclude("org.slf4j", "slf4j-api")
    }
    implementation("com.fifesoft:rsyntaxtextarea:3.4.0")
    implementation("org.json:json:20240303")
    implementation("org.xerial:sqlite-jdbc:3.45.3.0")
    implementation("org.openjdk.nashorn:nashorn-core:15.4")
    implementation("org.graalvm.sdk:graal-sdk:24.2.2")
    implementation("org.graalvm.js:js:24.2.2")
    compileOnly("org.projectlombok:lombok:0.11.0")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "250"
            untilBuild = "253.*"
        }

        changeNotes = """
      Initial version
    """.trimIndent()
    }
}

tasks {
    processResources {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
    }

    withType<JavaCompile> {
        options.annotationProcessorPath = configurations.annotationProcessor.get()
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
