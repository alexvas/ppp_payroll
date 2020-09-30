rootProject.name = "payroll"

pluginManagement {
    repositories {
        jcenter()
        mavenCentral()
        gradlePluginPortal()
    }
}

include(
        "domain",
        "repository",
        "business"
)

