plugins {
    java
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.hibernate.orm") version "6.4.4.Final"
    id("org.graalvm.buildtools.native") version "0.9.28"
    kotlin("jvm")
}

group = "et.nate"
version = "0.0.1-SNAPSHOT"

java {
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    val greenMailVersion = "2.0.1"
    // SPRING
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    // LOMBOK
    compileOnly("org.projectlombok:lombok")
    // DEV HELPERS
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    // RUNTIME
    runtimeOnly("org.postgresql:postgresql")
    // ANNOTATION
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")
    //TESTING
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("com.icegreen:greenmail-junit5:${greenMailVersion}")
    testImplementation("com.icegreen:greenmail-spring:${greenMailVersion}")

}

tasks.withType<Test> {
    useJUnitPlatform()
}

hibernate {
    enhancement {
        enableAssociationManagement.set(true)
    }
}

tasks.register("prepareKotlinBuildScriptModel") {}

tasks.register<Copy>("processFrontendResources") {

    val frontendProjectDir = project(":React").layout.projectDirectory
    val frontendDistDir = file("${frontendProjectDir}/dist")
    val frontendResourcesDir = project.layout.buildDirectory.dir("resources/main/static").get().asFile

    group = "Frontend"
    description = "Generates resources for frontend"
    dependsOn(":React:assembleFrontend")
    from(frontendDistDir)
    into(frontendResourcesDir)
}

tasks.named<Task>("processResources") {
    //dependsOn("processFrontendResources")
}
kotlin {
    jvmToolchain(21)
}
