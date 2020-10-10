import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version Project.kotlinVersion
    detekt
}

tasks.withType<Wrapper> {
    gradleVersion = Project.gradleVersion
}


allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = Plugin.detekt)

    group = "org.spectral.kasm"
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

    detekt {
        failFast = true
        config = files("${rootProject.projectDir}/config/detekt.yml")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = Project.jvmVersion
    }
}