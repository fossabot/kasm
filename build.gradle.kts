import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version Project.kotlinVersion
    detekt
}

tasks.withType<Wrapper> {
    gradleVersion = Project.gradleVersion
}

detekt {
    failFast = true
    buildUponDefaultConfig = true
    config = files("$projectDir/config/detekt.yml")
}


allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    group = "org.spectralpowered.kasm"
    version = Project.version

    repositories {
        mavenLocal()
        mavenCentral()
        jcenter()
    }

    dependencies {
        implementation(kotlin("stdlib"))
        implementation(Library.asm)
        implementation(Library.asmCommons)
        implementation(Library.asmUtil)
        testImplementation(Library.junitEngine)
        testImplementation(Library.junitApi)
        testImplementation(Library.mockk)
        testImplementation(kotlin("test"))
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = Project.jvmVersion
    }
}