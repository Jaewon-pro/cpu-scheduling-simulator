import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import proguard.gradle.ProGuardTask

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "com.example"
version = "1.0-SNAPSHOT"


repositories {
//    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}


kotlin {
    jvm {
        jvmToolchain(17)
        //withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        buildTypes.release.proguard {
            configurationFiles.from("proguard-rules.pro")
        }
        nativeDistributions {

            targetFormats(TargetFormat.Dmg, TargetFormat.Exe, TargetFormat.Deb)
            packageName = "CPU Scheduling Sim"
            packageVersion = "1.0.0"
            macOS {
                iconFile.set(project.file("icon.icns"))
            }
            windows {
                iconFile.set(project.file("icon.ico"))
            }
            linux {
                iconFile.set(project.file("icon.png"))
            }
        }
    }
}
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.guardsquare:proguard-gradle:7.2.0")
    }
}
// Define task to obfuscate the JAR and output to <name>.min.jar
tasks.register<ProGuardTask>("obfuscate") {
    val packageUberJarForCurrentOS by tasks.getting
    dependsOn(packageUberJarForCurrentOS)
    val files = packageUberJarForCurrentOS.outputs.files
    injars(files)
    outjars(files.map { file -> File(file.parentFile, "${file.nameWithoutExtension}.min.jar") })

    val library = if (System.getProperty("java.version").startsWith("1.")) "lib/rt.jar" else "jmods"
    libraryjars("${System.getProperty("java.home")}/$library")

    configuration("proguard-rules.pro")
}
