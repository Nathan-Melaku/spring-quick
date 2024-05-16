import org.siouan.frontendgradleplugin.infrastructure.gradle.RunPnpm

plugins {
    id("org.siouan.frontend-jdk17")
}

frontend {
    nodeVersion.set("20.13.1")
    assembleScript.set("run build")
}

tasks.register<RunPnpm>("start") {
    script.set("run start")
}