// Variables globales se definen gradle.properties
// Ktor y Kotlin
val ktor_version: String by project
val kotlin_version: String by project

// Logger
// val logback_version: String by project
val micrologging_version: String by project
val logbackclassic_version: String by project

// Cache
val cache_version: String by project

// Test
val junit_version: String by project
val mockk_version: String by project
val coroutines_version: String by project

// Koin
// val koin_version: String by project
val koin_ktor_version: String by project
val ksp_version: String by project
val koin_ksp_version: String by project

// BCrypt
val bcrypt_version: String by project


plugins {
    kotlin("jvm") version "1.8.0"
    id("io.ktor.plugin") version "2.2.2"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.0"
    id("com.google.devtools.ksp") version "1.8.0-1.0.8"
}

group = "joseluisgs.es"
version = "0.0.1"
application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor core
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")

    // Motor de Ktor
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")

    // Auth JWT
    implementation("io.ktor:ktor-server-auth-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-host-common-jvm:$ktor_version")

    // JSON content negotiation
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")

    // Content validation
    implementation("io.ktor:ktor-server-request-validation:$ktor_version")

    // Caching Headers
    implementation("io.ktor:ktor-server-caching-headers:$ktor_version")

    // Compression
    implementation("io.ktor:ktor-server-compression:$ktor_version")

    // CORS
    implementation("io.ktor:ktor-server-cors:$ktor_version")

    // WebSockets
    implementation("io.ktor:ktor-server-websockets:$ktor_version")

    // Certificados SSL y TSL
    implementation("io.ktor:ktor-network-tls-certificates:$ktor_version")

    // Logging
    // implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("ch.qos.logback:logback-classic:$logbackclassic_version")
    implementation("io.github.microutils:kotlin-logging-jvm:$micrologging_version")

    // Cache 4K para cachear datos de almacenamiento
    implementation("io.github.reactivecircus.cache4k:cache4k:$cache_version")

    // Koin
    implementation("io.insert-koin:koin-ktor:$koin_ktor_version") // Koin para Ktor
    implementation("io.insert-koin:koin-logger-slf4j:$koin_ktor_version") // Koin para Ktor con Logger
    // implementation("io.insert-koin:koin-core:$koin_version") // Koin Core no es necesario para Ktor, lo hemos añadido antes
    implementation("io.insert-koin:koin-annotations:$koin_ksp_version") // Si usamos Koin con KSP Anotaciones
    ksp("io.insert-koin:koin-ksp-compiler:$koin_ksp_version") // Si usamos Koin con KSP Anotaciones

    // BCrypt
    // implementation("de.nycode:bcrypt:2.2.0")
    implementation("org.mindrot:jbcrypt:$bcrypt_version")

    // Para testear
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version") // Usar deps de JUNIT 5 y no estas!

    // JUnit 5 en vez del por defecto de Kotlin...
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit_version")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junit_version")

    // MockK para testear Mockito con Kotlin
    testImplementation("io.mockk:mockk:$mockk_version")

    // Para testear métodos suspendidos o corrutinas
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutines_version")

    // Para testear con Koin
    // testImplementation("io.insert-koin:koin-test-junit5:$koin_version")
}

tasks.test {
    useJUnitPlatform()
}

// Para Koin Annotations, directorio donde se encuentran las clases compiladas
// KSP - To use generated sources
sourceSets.main {
    java.srcDirs("build/generated/ksp/main/kotlin")
}
