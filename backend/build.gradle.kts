plugins {
    java
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.hibernate.orm") version "6.4.4.Final"
    id("org.graalvm.buildtools.native") version "0.9.28"
}

group = "et.nate"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
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
    // SPRING
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    //implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    //implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
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

    val frontendProjectDir = project(":Angular").layout.projectDirectory
    val frontendDistDir = file("${frontendProjectDir}/dist/angular")
    val frontendResourcesDir = project.layout.buildDirectory.dir("resources/main/static").get().asFile
    println("frontend Build Dir: $frontendDistDir")
    println("frontend Resources Dir: $frontendResourcesDir")
    group = "Frontend"
    description = "Generates resources for frontend"
    dependsOn(":Angular:assembleFrontend")
    from(frontendDistDir)
    into(frontendResourcesDir)
}

tasks.named<Task>("processResources") {
    dependsOn("processFrontendResources")
}
