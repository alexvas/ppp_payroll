import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    //  application
}

repositories {
    jcenter()
}

group = "payroll"
version = "0.1"

dependencies {

    testImplementation("org.assertj:assertj-core:3.17.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testImplementation("io.mockk:mockk:1.10.2")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
}

/*
application {
    mainClassName = "MainKt"
}
*/

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xopt-in=kotlin.ExperimentalStdlibApi")
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
