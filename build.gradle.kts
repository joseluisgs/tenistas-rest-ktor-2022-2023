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
val koin_ktor_version: String by project
val ksp_version: String by project
val koin_ksp_version: String by project
val koin_version: String by project

// BCrypt
val bcrypt_version: String by project

// Bases de datos
val kotysa_version: String by project
val h2_r2dbc_version: String by project

// Ktow Swagger UI
val ktor_swagger_ui_version: String by project

// Result
val result_version: String by project


plugins {
    kotlin("jvm") version "1.8.0"
    id("io.ktor.plugin") version "2.2.2"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.0"
    id("com.google.devtools.ksp") version "1.8.0-1.0.8"
    id("org.jetbrains.dokka") version "1.7.20"
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
    // Para ktor-swagger-ui
    maven("https://jitpack.io")
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

    // Status Pages para coger las excepciones y devolverlas como queramos
    implementation("io.ktor:ktor-server-status-pages:$ktor_version")

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

    // Bases de datos
    // Reactividad con Kotysa
    implementation("org.ufoss.kotysa:kotysa-r2dbc-coroutines:$kotysa_version")
    // H2 R2DBC para usar H2 como base de datos
    runtimeOnly("io.r2dbc:r2dbc-h2:$h2_r2dbc_version")

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

    // Para testear con content negotiation
    testImplementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    // Auth para tokens usando el metodo de clienteAuth
    implementation("io.ktor:ktor-client-auth:$ktor_version")

    // Para documentar con Swagger y Open API sobre la marcha en nuestro codigo con DSL
    implementation("io.github.smiley4:ktor-swagger-ui:$ktor_swagger_ui_version")

    // Result
    implementation("com.michael-bull.kotlin-result:kotlin-result:$result_version")

    // Si queremos OpenAPI  generado por Ktor Team
    // implementation("io.ktor:ktor-server-openapi:$ktor_version")
    // Si queremos Swagger generado por Ktor Team
    // implementation("io.ktor:ktor-server-swagger:$ktor_version")

    // Para testear con Koin
    // testImplementation("io.insert-koin:koin-test:$koin_version")
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

// Para docker
ktor {
    docker {
        localImageName.set("tenistas-rest-ktor")
        imageTag.set("0.0.1-preview")
        jreVersion.set(io.ktor.plugin.features.JreVersion.JRE_17)
        portMappings.set(
            listOf(
                io.ktor.plugin.features.DockerPortMapping(
                    6969,
                    6969,
                    io.ktor.plugin.features.DockerPortMappingProtocol.TCP
                ),
                io.ktor.plugin.features.DockerPortMapping(
                    6963,
                    6963,
                    io.ktor.plugin.features.DockerPortMappingProtocol.TCP
                )
            )
        )
    }
}

// Vamos a usbirlo a Java 17
// https://kotlinlang.org/docs/get-started-with-jvm-gradle-project.html#explore-the-build-script
kotlin { // Extension for easy setup
    jvmToolchain(17) // Target version of generated JVM bytecode
}

