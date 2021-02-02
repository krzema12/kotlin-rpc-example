plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

group = "it.krzeminski.kotlinrpc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://dl.bintray.com/krzema1212/it.krzeminski")
    }
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
    }
    js {
        browser {
            binaries.executable()
            webpackTask {
                cssSupport.enabled = true
            }
            runTask {
                cssSupport.enabled = true
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.0")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-server-netty:1.4.0")
                implementation("it.krzeminski.kotlinrpc:kotlin-rpc:0.1.3")
            }
            kotlin.srcDirs(kotlin.srcDirs, "$buildDir/jvm/generated/")
        }
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.3.9")
            }
            kotlin.srcDirs(kotlin.srcDirs, "$buildDir/js/generated/")
        }
    }
}

val generateTodoAppApiJsProxy = tasks.register<JavaExec>("generateTodoAppApiJsClient") {
    group = "build"
    description = "Generate TodoApp JS proxy"
    classpath = sourceSets["main"].runtimeClasspath
    main = "it.krzeminski.kotlinrpc.api.generation.JsClientGenerationKt"
    args("it.krzeminski.todoapp.api.TodoAppApi", "$buildDir/js/generated")
}

val generateTodoAppApiJvmKtorServer = tasks.register<JavaExec>("generateTodoAppApiJvmKtorServer") {
    group = "build"
    description = "Generate TodoApp JVM Ktor server"
    classpath = sourceSets["main"].runtimeClasspath
    main = "it.krzeminski.kotlinrpc.api.generation.JvmKtorServerGenerationKt"
    args("it.krzeminski.todoapp.api.TodoAppApi", "$buildDir/jvm/generated")
}

tasks.getByName("jvmJar").dependsOn(generateTodoAppApiJvmKtorServer)
tasks.getByName("jsJar").dependsOn(generateTodoAppApiJsProxy)
