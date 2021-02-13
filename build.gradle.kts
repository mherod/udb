@file:Suppress(
    "UNUSED_VARIABLE",
    "SuspiciousCollectionReassignment"
)

import org.jetbrains.kotlin.config.KotlinCompilerVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilationToRunnableFiles
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import java.lang.System.getProperty
import java.lang.System.getenv

plugins {
    kotlin("multiplatform") version "1.4.30-M1"
    kotlin("plugin.serialization") version "1.4.30-M1"
}

repositories {
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    maven("https://kotlin.bintray.com/kotlinx")
}

val kotlinVersion: String = KotlinCompilerVersion.VERSION
val ktorVersion = "1.4.3"
val coroutinesVersion = "1.4.2"

group = "com.myunidays.udb"
version = "0.0.1"

repositories {
    mavenCentral()
}

kotlin {
    jvm()

    val hostOs = getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    targets.flatMap(KotlinTarget::compilations).forEach { compilation ->
        compilation.kotlinOptions {
            freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
        }
    }

    nativeTarget.apply {
        compilations {
            getByName("main").apply {
                enableEndorsedLibs = true
            }
        }
        binaries {
            executable {
                freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
                entryPoint = "com.myunidays.udb.cli.main"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3")
                implementation("com.autodesk:coroutineworker:0.6.2")
            }
        }
        val nativeMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion-native-mt")
            }
        }
        val jvmMain by getting {
            dependencies {
            }
        }
        val nativeTest by getting
        val jvmTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
                implementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
                implementation("junit:junit:4.13")
            }
        }
        all {
            languageSettings.apply {
                apiVersion = "1.4"
                enableLanguageFeature("InlineClasses")
                useExperimentalAnnotation("kotlin.RequiresOptIn")
                useExperimentalAnnotation("kotlinx.cli.ExperimentalCli")
            }
        }
    }
}

task<Exec>("cleanVcs") {
    workingDir(projectDir)
    inputs.files(fileTree(projectDir))
    outputs.files(fileTree(projectDir))
    commandLine("git", "clean", "-fXd", "$projectDir/")
}

task<Exec>("cleanEmptyDirs") {
    workingDir(projectDir)
    val projectSrc = "$projectDir/src/"
    inputs.files(fileTree(projectSrc))
    outputs.files(fileTree(projectSrc))
    commandLine("find", projectSrc, "-type", "d", "-empty", "-delete")
}

task<Copy>("installDebugBinary") {
    dependsOn(tasks.getByName("linkDebugExecutableNative"))
    from("$buildDir/bin/native/debugExecutable/")
    include("*.kexe")
    rename { it.substringBefore('.') }
    into("/usr/local/bin/")
}

task<Copy>("installReleaseBinary") {
    dependsOn(tasks.getByName("build"))
    from("$buildDir/bin/native/releaseExecutable/")
    include("*.kexe")
    rename { it.substringBefore('.') }
    into("/usr/local/bin/")
}

task<Copy>("installBrewBinary") {
    dependsOn(tasks.getByName("build"))
    from("$buildDir/bin/native/releaseExecutable/")
    include("*.kexe")
    rename { it.substringBefore('.') }
    into("${getenv("HOMEBREW_FORMULA_PREFIX")}/bin")
}

task<JavaExec>("run") {
    group = "run"
    dependsOn(tasks.getByName("jvmMainClasses"))
    main = "com.myunidays.udb.cli.MainKt"
    args("emulator", "--start")
    val jvm by kotlin.targets.getting
    val main: KotlinCompilation<KotlinCommonOptions> by jvm.compilations
    val runtimeDependencies = (main as KotlinCompilationToRunnableFiles<KotlinCommonOptions>).runtimeDependencyFiles
    classpath = files(main.output.allOutputs, runtimeDependencies)
}

tasks.withType<Test> {
    testLogging {
        showCauses = true
        showExceptions = true
        showStackTraces = true
        showStandardStreams = true
    }
}
