import org.jetbrains.kotlin.gradle.tasks.KotlinTest

plugins {
    java
}

tasks.create("testJar", Jar::class) {
    archiveName = "testJar"
}

tasks.withType<Test> {
    dependsOn(tasks.getByName("testJar"))
}