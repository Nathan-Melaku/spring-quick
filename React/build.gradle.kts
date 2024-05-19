import org.siouan.frontendgradleplugin.infrastructure.gradle.RunPnpm

plugins {
    id("org.siouan.frontend-jdk17")
}

frontend {
    nodeVersion.set("20.13.1")
    assembleScript.set("run build")
    nodeInstallDirectory.set(file(rootProject.ext.get("nodeInstallDirectory")!!))
}

tasks.register<RunPnpm>("dev") {
    script.set("dev")
}