pluginManagement {
    plugins {
        id("java")
        id("org.springframework.boot") version "3.2.5"
        id("io.spring.dependency-management") version "1.1.4"
        id("org.siouan.frontend-jdk17") version "8.0.0"
        kotlin("jvm") version "1.9.23"
    }
}

include("backend", "Angular", "React")
