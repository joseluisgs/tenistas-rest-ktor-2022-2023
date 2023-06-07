# Tenistas REST Ktor

Api REST de Tenistas con Ktor para Programación de Servicios y Procesos de 2º de DAM. Curso 2022/2023

[![Kotlin](https://img.shields.io/badge/Code-Kotlin-blueviolet)](https://kotlinlang.org/)
[![LICENSE](https://img.shields.io/badge/Lisence-CC-%23e64545)](https://joseluisgs.dev/docs/license/)
![GitHub](https://img.shields.io/github/last-commit/joseluisgs/tenistas-rest-ktor-2022-2023)

![imagen](./images/ktor.png)

- [Tenistas REST Ktor](#tenistas-rest-ktor)
  - [Descripción](#descripción)
    - [Advertencia](#advertencia)
    - [Tecnologías](#tecnologías)
  - [Dominio](#dominio)
    - [Representante](#representante)
    - [Raqueta](#raqueta)
    - [Tenista](#tenista)
    - [Usuario](#usuario)
  - [Proyectos y documentación anteriores](#proyectos-y-documentación-anteriores)
  - [Arquitectura](#arquitectura)
  - [Endpoints](#endpoints)
    - [Representantes](#representantes)
    - [Raquetas](#raquetas)
    - [Tenistas](#tenistas)
    - [Usuarios](#usuarios)
    - [Storage](#storage)
    - [Test](#test)
  - [Ktor](#ktor)
    - [Creando un proyecto](#creando-un-proyecto)
    - [Punto de Entrada](#punto-de-entrada)
    - [Parametrizando la aplicación](#parametrizando-la-aplicación)
    - [Usando Plugins](#usando-plugins)
    - [Creando rutas](#creando-rutas)
      - [Type-Safe Routing y Locations](#type-safe-routing-y-locations)
    - [Serialización y Content Negotiation](#serialización-y-content-negotiation)
    - [Otros plugins](#otros-plugins)
      - [Cache Headers](#cache-headers)
      - [Comprensión de contenido](#comprensión-de-contenido)
      - [CORS](#cors)
    - [Responses](#responses)
      - [Enviando datos serializados](#enviando-datos-serializados)
    - [Requests](#requests)
      - [Parámetros de ruta](#parámetros-de-ruta)
      - [Parámetros de consulta](#parámetros-de-consulta)
      - [Peticiones datos serializados](#peticiones-datos-serializados)
      - [Peticiones con formularios](#peticiones-con-formularios)
      - [Peticiones multiparte](#peticiones-multiparte)
      - [Subida de información](#subida-de-información)
      - [Request validation](#request-validation)
      - [Status Pages](#status-pages)
    - [Excepciones personalizadas](#excepciones-personalizadas)
    - [Gestiones de Errores con Result](#gestiones-de-errores-con-result)
    - [WebSockets](#websockets)
    - [SSL y Certificados](#ssl-y-certificados)
    - [Autenticación y Autorización con JWT](#autenticación-y-autorización-con-jwt)
    - [Testing](#testing)
    - [Despliegue](#despliegue)
      - [JAR](#jar)
      - [Aplicación](#aplicación)
      - [Docker](#docker)
    - [Documentación](#documentación)
  - [Reactividad](#reactividad)
  - [Inmutabilidad](#inmutabilidad)
  - [Caché](#caché)
  - [Notificaciones en tiempo real](#notificaciones-en-tiempo-real)
  - [Proveedor de Dependencias](#proveedor-de-dependencias)
  - [Railway Oriented Programming](#railway-oriented-programming)
  - [Seguridad de las comunicaciones](#seguridad-de-las-comunicaciones)
    - [SSL/TLS](#ssltls)
    - [Autenticación y Autorización con JWT](#autenticación-y-autorización-con-jwt-1)
    - [CORS](#cors-1)
    - [BCrypt](#bcrypt)
  - [Testing](#testing-1)
    - [Postman](#postman)
  - [Distribución y Despliegue](#distribución-y-despliegue)
  - [Documentación](#documentación-1)
  - [Recursos](#recursos)
  - [Autor](#autor)
    - [Contacto](#contacto)
    - [¿Un café?](#un-café)
  - [Licencia de uso](#licencia-de-uso)

## Descripción

El siguiente proyecto es una API REST de Tenistas con Ktor para Programación de Servicios y Procesos de 2º de DAM. Curso
2022/2023. En ella se pretende crear un servicio completo para la gestión de tenistas, raquetas y representantes de
marcas de raquetas.

El objetivo es que el alumnado aprenda a crear un servicio REST con Ktor, con las operaciones CRUD, securizar el
servicio con JWT y usar un cliente para consumir el servicio. Se pretende que el servicio completo sea asíncrono y
reactivo en lo máximo posible agilizando el servicio mediante una caché.

Además que permita escuchar cambios en tiempo real usando websocket

Se realizará inyección de dependencias y un sistema de logging.

Tendrá una página web de presentación como devolución de recursos estáticos.

Este proyecto tiene a su "gemelo" implementando en
Ktor: [tenistas-rest-springboot-2022-2023](https://github.com/joseluisgs/tenistas-rest-springboot-2022-2023)

### Advertencia

Esta API REST no está pensada para ser usada en producción. Es un proyecto de aprendizaje y por tanto algunas cosas no
se profundizan y otras están pensadas para poder realizarlas en clase de una manera más simple con el objetivo que el
alumnado pueda entenderlas mejor. No se trata de montar la mejor arquitectura o el mejor servicio, sino de aprender a
crear un servicio REST en el tiempo exigido por el calendario escolar.

Este proyecto está en constante evolución y se irán añadiendo nuevas funcionalidades y mejoras para el alumnado. De la
misma manera se irá completando la documentación asociada.

Si quieres colaborar, puedes hacerlo contactando [conmigo](#contacto).

### Tecnologías

- Servidor Web: [Ktor](https://ktor.io/) - Framework para crear servicios web en Kotlin asíncronos y multiplataforma.
- Autenticación: [JWT](https://jwt.io/) - JSON Web Token para la autenticación y autorización.
- Encriptado: [Bcrypt](https://en.wikipedia.org/wiki/Bcrypt) - Algoritmo de hash para encriptar contraseñas.
- Proveedor de dependencias: [Koin](https://insert-koin.io/) - Framework para la inyección de dependencias.
- Asincronía: [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) - Librería de Kotlin para la
  programación asíncrona.
- Result: [Railway Oriented Programming](https://fsharpforfunandprofit.com/rop/) - Patrón de programación para el
  control de errores.
- Logger: [Kotlin Logging](https://github.com/MicroUtils/kotlin-logging) - Framework para la gestión de logs.
- Caché: [Cache4k](https://reactivecircus.github.io/cache4k/) - Versión 100% Kotlin asíncrona y multiplataforma
  de [Caffeine](https://github.com/ben-manes/caffeine).
- Base de datos: [H2](https://www.h2database.com/) - Base de datos relacional que te permite trabajar en memoria,
  fichero y servidor.
- Librería base de Datos: [Kotysa](https://ufoss.org/kotysa/kotysa.html) - Librería para la gestión de bases de datos en
  Kotlin que te permite operar reactivamente bajo [R2DBC](https://r2dbc.io/).
- Notificaciones en tiempo real: [Ktor WebSockets](https://ktor.io/docs/websocket.html) - Framework para la gestión de
  websockets.
- Testing: [JUnit 5](https://junit.org/junit5/) - Framework para la realización de tests
  unitarios, [Mockk](https://mockk.io/) librería de Mocks para Kotlin, así como las propias herramientas de Ktor.
- Cliente: [Postman](https://www.postman.com/) - Cliente para realizar peticiones HTTP.
- Contenedor: [Docker](https://www.docker.com/) - Plataforma para la creación y gestión de contenedores.
- Documentación: [Dokka](https://kotlinlang.org/docs/dokka-introduction.html) y [Swagger](https://swagger.io/) -
  Herramienta para la generación de documentación y pruebas de API REST respectivamente
  mediante [OpenAPI](https://www.openapis.org/).

## Dominio

Gestionar tenistas, raquetas y representantes de marcas de raquetas. Sabemos que:

- Una raqueta tiene un representante y el representante es solo de una marca de raqueta (1-1). No puede haber raquetas
  sin representante y no puede haber representantes sin raquetas.
- Un tenista solo puede o no tener contrato con una raqueta y una raqueta o modelo de raqueta puede ser usada por varios
  tenistas (1-N). Puede haber tenistas sin raqueta y puede haber raquetas sin tenistas.
- Por otro lado tenemos usuarios con roles de administrador y usuarios que se pueden registrar, loguear consultar los
  datos y acceder a los datos de los usuarios (solo administradores).

### Representante

| Campo  | Tipo   | Descripción              |
|--------|--------|--------------------------|
| id     | UUID   | Identificador único      |
| nombre | String | Nombre del representante |
| email  | String | Email del representante  |

### Raqueta

| Campo         | Tipo          | Descripción                           |
|---------------|---------------|---------------------------------------|
| id            | UUID          | Identificador único                   |
| marca         | String        | Marca de la raqueta                   |
| precio        | Double        | Precio de la raqueta                  |
| representante | Representante | Representante de la raqueta (no nulo) |

### Tenista

| Campo           | Tipo      | Descripción                                    |
|-----------------|-----------|------------------------------------------------|
| id              | UUID      | Identificador único                            |
| nombre          | String    | Nombre del tenista                             |
| ranking         | Int       | Ranking del tenista                            |
| fechaNacimiento | LocalDate | Fecha de nacimiento del tenista                |
| añoProfesional  | Int       | Año en el que se convirtió en profesional      |
| altura          | Double    | Altura del tenista                             |
| peso            | Double    | Peso del tenista                               |
| manoDominante   | String    | Mano dominante del tenista (DERECHA/IZQUIERDA) |
| tipoReves       | String    | Tipo de revés del tenista (UNA_MANO/DOS_MANOS) |
| puntos          | Int       | Puntos del tenista                             |
| pais            | String    | País del tenista                               |
| raquetaID       | UUID      | Identificador de la raqueta (puede ser nulo)   |

### Usuario

| Campo    | Tipo   | Descripción                    |
|----------|--------|--------------------------------|
| id       | UUID   | Identificador único            |
| nombre   | String | Nombre del usuario             |
| email    | String | Email del usuario              |
| username | String | Rol del usuario                |
| password | String | Contraseña del usuario         |
| avatar   | String | Avatar del usuario             |
| rol      | Rol    | Rol del usuario (ADMIN o USER) |

## Proyectos y documentación anteriores

Parte de los contenidos a desarrollar en este proyecto se han desarrollado en proyectos anteriores. En este caso:

- [Kotlin-Ktor-REST-Service](https://github.com/joseluisgs/Kotlin-Ktor-REST-Service)
- [SpringBoot-Productos-REST-DAM-2021-2022](https://github.com/joseluisgs/SpringBoot-Productos-REST-DAM-2021-2022)

Para la parte de reactividad te recomiendo
leer: ["Ya no sé programar si no es reactivo"](https://joseluisgs.dev/blogs/2022/2022-12-06-ya-no-se-programar-sin-reactividad.html)

## Arquitectura

Nos centraremos en la arquitectura de la API REST. Para ello, usaremos el patrón de diseño MVC (Modelo Vista
Controlador) en capas.

![img_1.png](./images/layers.png)

![img_2.png](./images/expla.png)

## Endpoints

Recuerda que puedes conectarte de forma segura:

- Para la API REST: http://localhost:6969/api y https://localhost:6963/api
- Para la página web estática: http://localhost:6969/web y https://localhost:6963/web

Los endpoints que vamos a usar a nivel de api, parten de /api/ y puedes usarlos con tu cliente favorito. En este caso,
usaremos Postman:

### Representantes

| Método | Endpoint (/api)                  | Auth | Descripción                                                                    | Status Code (OK) | Content    |
|--------|----------------------------------|------|--------------------------------------------------------------------------------|------------------|------------|
| GET    | /representantes                  | No   | Devuelve todos los representantes                                              | 200              | JSON       |
| GET    | /representantes?page=X&perPage=Y | No   | Devuelve representantes paginados                                              | 200              | JSON       |
| GET    | /representantes/{id}             | No   | Devuelve un representante por su id                                            | 200              | JSON       |
| POST   | /representantes                  | No   | Crea un nuevo representante                                                    | 201              | JSON       |
| PUT    | /representantes/{id}             | No   | Actualiza un representante por su id                                           | 200              | JSON       |
| DELETE | /representantes/{id}             | No   | Elimina un representante por su id                                             | 204              | No Content |
| GET    | /representantes/find?nombre=X    | No   | Devuelve los representantes con nombre X                                       | 200              | JSON       |
| WS     | /updates/representantes          | No   | Websocket para notificaciones los cambios en los representantes en tiempo real | ---              | JSON       |

### Raquetas

| Método | Endpoint (/api)              | Auth | Descripción                                                              | Status Code (OK) | Content    |
|--------|------------------------------|------|--------------------------------------------------------------------------|------------------|------------|
| GET    | /raquetas                    | No   | Devuelve todas las raquetas                                              | 200              | JSON       |
| GET    | /raquetas?page=X&perPage=Y   | No   | Devuelve raquetas paginadas                                              | 200              | JSON       |
| GET    | /raquetas/{id}               | No   | Devuelve una raqueta por su id                                           | 200              | JSON       |
| POST   | /raquetas                    | No   | Crea una nueva raqueta                                                   | 201              | JSON       |
| PUT    | /raquetas/{id}               | No   | Actualiza una raqueta por su id                                          | 200              | JSON       |
| DELETE | /raquetas/{id}               | No   | Elimina una raqueta por su id                                            | 204              | No Content |
| GET    | /raquetas/find?marca=X       | No   | Devuelve las raquetas con marca X                                        | 200              | JSON       |
| GET    | /raquetas/{id}/representante | No   | Devuelve el representante de la raqueta dado su id                       | 200              | JSON       |
| WS     | /updates/raquetas            | No   | Websocket para notificaciones los cambios en las raquetas en tiempo real | ---              | JSON       |

### Tenistas

| Método | Endpoint (/api)             | Auth | Descripción                                                              | Status Code (OK) | Content    |
|--------|-----------------------------|------|--------------------------------------------------------------------------|------------------|------------|
| GET    | /tenistas                   | No   | Devuelve todos los tenistas                                              | 200              | JSON       |
| GET    | /tenistas?page=X&perPage=Y  | No   | Devuelve tenistas paginados                                              | 200              | JSON       |
| GET    | /tenistas/{id}              | No   | Devuelve un tenista por su id                                            | 200              | JSON       |
| POST   | /tenistas                   | No   | Crea un nuevo tenista                                                    | 201              | JSON       |
| PUT    | /tenistas/{id}              | No   | Actualiza un tenista por su id                                           | 200              | JSON       |
| DELETE | /tenistas/{id}              | No   | Elimina un tenista por su id                                             | 204              | No Content |
| GET    | /tenistas/find?nombre=X     | No   | Devuelve los tenistas con nombre X                                       | 200              | JSON       |
| GET    | /tenistas/{id}/raqueta      | No   | Devuelve la raqueta del tenista dado su id                               | 200              | JSON       |
| GET    | /tenistas/ranking/{ranking} | No   | Devuelve el tenista con ranking X                                        | 200              | JSON       |
| WS     | /updates/tenistas           | No   | Websocket para notificaciones los cambios en los tenistas en tiempo real | ---              | JSON       |

### Usuarios

| Método | Endpoint (/api) | Auth | Descripción                                                   | Status Code (OK) | Content |
|--------|-----------------|------|---------------------------------------------------------------|------------------|---------|
| POST   | /users/login    | No   | Login de un usuario, Token                                    | 200              | JSON    |
| POST   | /users/register | No   | Registro de un usuario                                        | 201              | JSON    |
| GET    | /users/me       | JWT  | Datos del usuario del token                                   | 200              | JSON    |
| PUT    | /users/me       | JWT  | Actualiza datos del usuario: nombre, e-mail y username        | 200              | JSON    |
| PATCH  | /users/me       | JWT  | Actualiza avatar del usuario como multipart                   | 200              | JSON    |
| GET    | /users/list     | JWT  | Devuelve todos los usuarios, si el token pertenece a un admin | 200              | JSON    |

### Storage

| Método | Endpoint (/api)     | Auth | Descripción                           | Status Code (OK) | Content    |
|--------|---------------------|------|---------------------------------------|------------------|------------|
| GET    | /storage/check      | NO   | Info del servicio                     | 200              | JSON       |
| POST   | /storage            | No   | Envía un fichero como stream de bytes | 201              | JSON       |
| GET    | /storage/{fileName} | No   | Descarga un fichero por su nombre     | 200              | JSON       |
| DELETE | /storage/{fileName} | JWT  | Elimina un fichero por su nombre      | 204              | No Content |

### Test

| Método | Endpoint (/api) | Auth | Descripción                                                        | Status Code (OK) | Content    |
|--------|-----------------|------|--------------------------------------------------------------------|------------------|------------|
| GET    | /test?texto     | No   | Devuelve un JSON con datos de prueba, y el texto de query opcional | 200              | JSON       |
| GET    | /test/{id}      | No   | Devuelve un JSON con datos de prueba por su id                     | 200              | JSON       |
| POST   | /test           | No   | Crea un nuevo JSON con datos de prueba                             | 201              | JSON       |
| PUT    | /test/{id}      | No   | Actualiza un JSON con datos de prueba por su id                    | 200              | JSON       |
| PATCH  | /test/{id}      | No   | Actualiza un JSON con datos de prueba por su id                    | 200              | JSON       |
| DELETE | /test/{id}      | No   | Elimina un JSON con datos de prueba por su id                      | 204              | No Content |

## Ktor

[Ktor](https://ktor.io/) es el framework para desarrollar servicios y clientes asincrónicos. Es
100% [Kotlin](https://kotlinlang.org/) y se ejecuta en
usando [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html). Admite proyectos multiplataforma, lo que
significa que puede usarlo para cualquier proyecto dirigido a JVM, Android, iOS, nativo o Javascript. En este proyecto
aprovecharemos Ktor para crear un servicio web para consumir una API REST. Además, aplicaremos Ktor para devolver
páginas web.

Ktor trabaja con un sistema de plugins que lo hacen muy flexible y fácil de configurar. Además, Ktor es un framework
donde trabajamos con DSL (Domain Specific Language) que nos permite crear código de forma más sencilla y legible.

Además, permite adaptar su estructura en base a funciones de extensión.

![img_3.png](./images/ktor_logo.svg)

### Creando un proyecto

Podemos crear un proyecto Ktor usando el plugin IntelliJ, desde su web. Con estos [asistentes](https://ktor.io/create/)
podemos crear un proyecto Ktor con las opciones que queramos (plugins), destacamos el routing, el uso de json, etc.

### Punto de Entrada

El servidor tiene su entrada y configuración en la clase Application. Esta lee la configuración en base
al [fichero de configuración](./src/main/resources/application.conf) y a partir de aquí se crea una instancia de la
clase Application en base a la configuración de module().

### Parametrizando la aplicación

Podemos parametrizar la aplicación usando el fichero de configuración. En este caso, usaremos el fichero de
configuración .conf y puede ser en distintos formatos, como JSON, YAML o HOCON. En este caso, usaremos HOCON. En este
fichero de configuración podemos definir distintas propiedades, como el puerto de escucha, el host, el tiempo de
expiración del token JWT, o el modo [Auto-Reload](https://ktor.io/docs/auto-reload.html), etc. En este caso, usaremos el
siguiente fichero de configuración:

```hocon
ktor {
    ## Para el puerto
    deployment {
        port = 6969
        port = ${?PORT}
    }

    ## Para la clase principal
    application {
        modules = [ joseluisgs.es.ApplicationKt.module ]
    }

    ## Modo de desarrollo, se dispara cuando detecta cambios
    ## development = true
    deployment {
        ## Directorios a vigilar
        watch = [ classes, resources ]
    }

    ## Modo de ejecución
    environment = dev
    environment = ${?KTOR_ENV}
}
```

### Usando Plugins

Ktor se puede extender y ampliar usando plugins. Estos plugins se "instalan" y configuran configuran según las
necesidades.
Los más recomendados para hacer una Api Rest son:

- Routing: Para definir las rutas de la API
- Serialization: Para serializar y deserializar objetos, por ejemplo en JSON
- ContentNegotiation: Para definir el tipo de contenido que se va a usar en la API, por ejemplo JSON

```kotlin
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        // Lo ponemos bonito :)
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }
}
```

### Creando rutas

Las [rutas](https://ktor.io/docs/routing-in-ktor.html) se definen creando una función de extensión sobre Route. A su
vez, usando DSL se definen las rutase en base a
las petición HTTP sobre ella. Podemos responder a la petición usando call.respondText(), para texto; call.respondHTML(),
para contenido HTML usando [Kotlin HTML DSL](https://github.com/Kotlin/kotlinx.html); o call.respond() para devolver una
respuesta en formato JSON o XML.
finalmente asignamos esas rutas a la instancia de Application, es decir, dentro del método module(). Un ejemplo de ruta
puede ser:

```kotlin
routing {
    // Entrada en la api
    get("/") {
        call.respondText("👋 Hola Kotlin REST Service con Kotlin-Ktor")
    }
}
```

#### Type-Safe Routing y Locations

Ktor te permite hacer [Type-Safe Routing](https://ktor.io/docs/type-safe-routing.html), es decir, que puedes definir una
clase que represente una ruta y que tenga las operaciones a realizar.

También podemos crear rutas de manera tipada con [Locations](https://ktor.io/docs/locations.html), pero esta siendo
sustituida por Type-Safe Routing.

### Serialización y Content Negotiation

Ktor soporta [Content Negotiation](https://ktor.io/docs/serialization.html), es decir, que puede aceptar peticiones y
respuestas distintos tipos de contenido, como JSON, XML, HTML, etc. En este caso, usaremos JSON. Para ello, usaremos la
librería [Kotlinx Serialization](https://kotlinlang.org/docs/serialization.html)

```kotlin
install(ContentNegotiation) {
    json(Json {
        prettyPrint = true
        isLenient = true
    })
}
```

### Otros plugins

#### Cache Headers

Nos permite [configurar](https://ktor.io/docs/caching.html) los encabezados Cache-Control y Expires utilizados para el
almacenamiento en caché de HTTP. Puede configurar el almacenamiento en caché de las siguientes maneras: globales,
particulares a nivel de ruta o llamada, activando o desactivando esta opción para determinados tipos de contenidos.

#### Comprensión de contenido

Ktor proporciona la capacidad de [comprimir contenido](https://ktor.io/docs/compression.html) saliente usando diferentes
algoritmos de compresión, incluidos gzip y deflate, y con ello, especificar las condiciones requeridas para comprimir
datos (como un tipo de contenido o tamaño de respuesta) o incluso comprimir datos en función de parámetros de solicitud
específicos.

#### CORS

Si se supone que su servidor debe manejar solicitudes de origen
cruzado ([CORS](https://developer.mozilla.org/es/docs/Web/HTTP/CORS)), debe instalar y configurar
el [complemento CORS](https://ktor.io/docs/cors.html) Ktor. Este complemento le permite configurar hosts permitidos,
métodos HTTP, encabezados establecidos por el cliente, etc.

Por defecto, el plugin de CORS permite los métodos GET, POST y HEAD

Lo ideal es que aprendas a configurarlo según tus necesidades, pero aquí tienes un ejemplo de configuración básica:

```kotlin
install(CORS) {
    // podemos permitir algún host específico
    anyHost() // cualquier host, quitar en produccion
    allowHost("client-host")
    allowHost("client-host:8081")
    allowHost("client-host", subDomains = listOf("en", "de", "es"))
    allowHost("client-host", schemes = listOf("http", "https"))

    // Podemos permitir contenido
    allowHeader(HttpHeaders.ContentType) // Permitimos el tipo de contenido
    allowHeader(HttpHeaders.Authorization) // Permitimos autorithachion

    // Si queremos permitir otros métodos
    allowMethod(HttpMethod.Options)
    allowMethod(HttpMethod.Put)
    allowMethod(HttpMethod.Patch)
    allowMethod(HttpMethod.Delete)
}
```

### Responses

En Ktor podemos mandar distintos tipos de [respuesta](https://ktor.io/docs/responses.html), así como distintos códigos
de [estado](https://ktor.io/docs/responses.html#status).

```kotlin
call.respondText("👋 Hola Kotlin REST Service con Kotlin-Ktor")
call.respond(HttpStatusCode.OK, "👋 Hola Kotlin REST Service con Kotlin-Ktor")
call.respond(HttpStatusCode.NotFound, "No encontrado")
```

#### Enviando datos serializados

Simplemente usa una data class y la función call.respond() para enviar datos serializados. En este caso, usaremos la
librería [Kotlinx Serialization](https://kotlinlang.org/docs/serialization.html)

```kotlin
@Serializable
data class Customer(val id: Int, val firstName: String, val lastName: String)

get("/customer") {
    call.respond(Customer(1, "José Luis", "García Sánchez"))
}
```

### Requests

En Ktor podemos recibir distintos tipos de [peticiones](https://ktor.io/docs/requests.html).

#### Parámetros de ruta

Podemos obtener los parámetros del Path, con parameters, como en el siguiente ejemplo, siempre y cuando estén definidos
en la ruta {param}:

```kotlin
get("/hello/{name}") {
    val name = call.parameters["name"]
    call.respondText("Hello $name!")
}
```

#### Parámetros de consulta

Podemos obtener los parámetros de la Query, con queryParameters, si tenemos por ejemplo la siguiente ruta:
/products?price=asc&category=1:

```kotlin
get("/products") {
    val price = call.request.queryParameters["price"]
    val category = call.request.queryParameters["category"]
    call.respondText("Price: $price, Category: $category")
}
```

#### Peticiones datos serializados

Para recibir datos serializados, usa la función call.receive() y la data class que representa el tipo de datos que se
espera recibir con la que casteamos el body de la petición. En este caso, usaremos la
librería [Kotlinx Serialization](https://kotlinlang.org/docs/serialization.html)

```kotlin
@Serializable
data class Customer(val id: Int, val firstName: String, val lastName: String)

post("/customer") {
    val customer = call.receive<Customer>()
    call.respondText("Customer: $customer")
}
```

#### Peticiones con formularios

Ktor soporta [peticiones con formularios](https://ktor.io/docs/requests.html#form_parameters), es decir, que podemos
enviar datos de un formulario.

```kotlin
post("/signup") {
    val formParameters = call.receiveParameters()
    val username = formParameters["username"].toString()
    call.respondText("The '$username' account is created")
}
```

#### Peticiones multiparte

Ktor soporta [peticiones multipartes](https://ktor.io/docs/requests.html#form_data), es decir, que podemos enviar
ficheros, imágenes, etc.

```kotlin
post("/upload") {
    //  multipart data (suspending)
    val multipart = call.receiveMultipart()
    multipart.forEachPart { part ->
        val fileName = part.originalFileName as String
        var fileBytes = part.streamProvider().readBytes()
        File("uploads/$fileName").writeBytes(fileBytes)
        part.dispose()
    }
    call.respondText("$fileName is uploaded to 'uploads/$fileName'")
}
```

#### Subida de información

Ktor soporta [subida de información](https://ktor.io/docs/requests.html#body_contents), es decir, que podemos enviar
ficheros, imágenes, etc. Podemos hacerlo con recieve o receiveChannel() (raw). Para el caso de ficheros se puede mandar
así si sabemos cómo almacenarlos, si no podemos enviar ficheros usando el sistema
de [petición multipart](#peticiones-multiparte).

```kotlin
post("/upload") {
    val file = File("uploads/ktor_logo.png")
    call.receiveChannel().copyAndClose(file.writeChannel())
    call.respondText("A file is uploaded")
}
```

#### Request validation

Ktor tiene una [API de validación](https://ktor.io/docs/request-validation.html) que nos permite validar los datos del
body de una petición. En este caso lanzando RequestValidationException si no es correcto.

```kotlin
install(RequestValidation) {
    validate<Customer> { customer ->
        if (customer.id <= 0)
            ValidationResult.Invalid("A customer ID should be greater than 0")
        else ValidationResult.Valid
    }
}
```

#### Status Pages
Ktor nos ofrece poder [personalizar las páginas de error](https://ktor.io/docs/status-pages.html) que se muestran al usuario.

De esta manera, a la hora de trabajar con las excepciones podemos desviarlas a este sistema y ofrecer una respuesta con su código de estado de error correspondiente y un mensaje personalizado.

```kotlin
install(StatusPages) {
    exception<AuthenticationException> { cause ->
        call.respond(HttpStatusCode.Unauthorized, "Not Authenticated")
    }
    exception<AuthorizationException> { cause ->
        call.respond(HttpStatusCode.Forbidden, "Not Authorized")
    }
    exception<UserException.NotFound> { cause ->
        call.respond(HttpStatusCode.NotFound, cause.message)
    }

}
```

### Excepciones personalizadas
Aunque no es la mejor técnica, pues hay otras mejores como Railway Oriented Programming, podemos usar excepciones personalizadas para controlar los errores de nuestra aplicación.

Podemos lanzarlas con throw, y capturarlas con try/catch, o podemos usar la Status Pages para capturarlas y devolver una respuesta predeterminada

```kotlin
sealed class RaquetaException(message: String) : RuntimeException(message) {
    class NotFound(message: String) : RaquetaException(message)
    class BadRequest(message: String) : RaquetaException(message)
    class ConflictIntegrity(message: String) : RaquetaException(message)
    class RepresentanteNotFound(message: String) : RaquetaException(message)
}
```

```kotlin
override suspend fun findById(id: UUID): Raqueta {
    logger.debug { "findById: Buscando raqueta en servicio con id: $id" }

    return repository.findById(id)
        ?: throw RaquetaException.NotFound("No se ha encontrado la raqueta con id: $id")

}
```

### Gestiones de Errores con Result
Para evitar que las excepciones se propaguen por la aplicación, podemos usar el patrón Result, que nos permite devolver un valor o un error, siguiendo la filosofía de Railway Oriented Programming. De esta manera, podemos controlar los errores en la capa de servicio, y devolver un valor o un error, que será gestionado en la capa de controladores. De esta manera tendremos un control de errores centralizado, y evitaremos que las excepciones se propaguen por la aplicación.

Además, tener una jerarquía de errores nos permite tener un control de errores más granular, y poder devolver un código de error más específico.


```kotlin

sealed class RaquetaError(val message: String) {
    class NotFound(message: String) : RaquetaError(message)
    class BadRequest(message: String) : RaquetaError(message)
    class ConflictIntegrity(message: String) : RaquetaError(message)
    class RepresentanteNotFound(message: String) : RaquetaError(message)
}


override suspend fun findByUuid(uuid: UUID): Result<Raqueta, RaquetaError> {
    logger.debug { "Servicio de raquetas findByUuid con uuid: $uuid" }

    return raquetasRepository.findByUuid(uuid)
        ?.let { Ok(it) }
        ?: Err(RaquetaError.NotFound("No se ha encontrado la raqueta con uuid: $uuid"))
}

@GetMapping("/{id}")
suspend fun findById(@PathVariable id: UUID): ResponseEntity<RaquetaDto> {
    logger.info { "GET By ID Raqueta con id: $id" }

    raquetasService.findByUuid(id).mapBoth(
        success = {
            return ResponseEntity.ok(
                it.toDto(
                    raquetasService.findRepresentante(it.representanteId).get()!!
                )
            )
        },
        failure = { return handleErrors(it) }
    )
}

```

### WebSockets

Ktor soporta [WebSockets](https://developer.mozilla.org/es/docs/Web/API/WebSockets_API) para crear aplicaciones que
hagan uso de ellos. Los [WebSockets](https://ktor.io/docs/websocket.html) permiten crear aplicaciones que requieren
transferencia de datos en tiempo real desde y hacia el servidor ya que que hace posible abrir una sesión de comunicación
interactiva entre el navegador del usuario y un servidor. Con esta API, puede enviar mensajes a un servidor y recibir
respuestas controladas por eventos sin tener que consultar al servidor para una respuesta.

```kotlin
webSocket("/echo") {
    send("Please enter your name")
    for (frame in incoming) {
        frame as? Frame.Text ?: continue
        val receivedText = frame.readText()
        if (receivedText.equals("bye", ignoreCase = true)) {
            close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
        } else {
            send(Frame.Text("Hi, $receivedText!"))
        }
    }
}

```

### SSL y Certificados

Aunque lo normal, es que nuestros servicios estén detrás de un Proxy Inverso, podemos configurar Ktor para
que [soporte SSL](https://ktor.io/docs/ssl.html) y certificados. Para ello, debemos añadir la librería de soporte para
TSL, y configurar el puerto y el certificado en el fichero application.conf.

```hocon
ktor {
    ## Para el puerto
    deployment {
        ## Si no se especifica el puerto, se usa el 8080, si solo queremos SSL quitar el puerto normal
        port = 6969
        port = ${?PORT}
        ## Para SSL, si es necesario poner el puerto
        sslPort = 6963
        sslPort = ${?SSL_PORT}
    }

    ## Para la clase principal
    application {
        modules = [ joseluisgs.es.ApplicationKt.module ]
    }

    ## Para SSL/TSL configuración del llavero y certificado
    security {
        ssl {
            keyStore = ./cert/server_keystore.p12
            keyAlias = serverKeyPair
            keyStorePassword = 1234567
            privateKeyPassword = 1234567
        }
    }
}
```

### Autenticación y Autorización con JWT

Ktor tiene una [API de autenticación](https://ktor.io/docs/authentication.html) que nos permite autenticar usuarios y
autorizar peticiones. En este caso, usaremos [JWT](https://jwt.io/) para la autenticación y autorización. Para ello,
debemos añadir la librería de soporte para [Ktor JWT](https://ktor.io/docs/jwt.html) y configurar sus opciones.

Gracias a ella podemos crear un interceptor (middleware) que se ejecutará antes de cada petición y que nos permitirá
validar el token JWT y añadirlo a la petición para que podamos usarlo en el resto de la aplicación. En este caso,
usaremos el token para añadir el usuario autenticado a la petición y poder usarlo en el resto de la aplicación.

```kotlin
// Instalamos el interceptor de autenticación
install(Authentication) {
    jwt("auth-jwt") {
        validate { credential ->
            if (credential.payload.getClaim("username").asString() != "") {
                JWTPrincipal(credential.payload)
            } else {
                null
            }
        }
    }
}

// Añadimos el interceptor a todas las rutas
routing {
    authenticate("auth-jwt") {
        get("/hello") {
            val principal = call.principal<JWTPrincipal>() // Leemos el token
            val username = principal!!.payload.getClaim("username").asString()
            val expiresAt = principal.expiresAt?.time?.minus(System.currentTimeMillis())
            call.respondText("Hello, $username! Token is expired at $expiresAt ms.")
        }
    }
}

```

### Testing

Ktor tiene una [API de testing](https://ktor.io/docs/testing.html) que nos permite testear nuestras aplicaciones. Para
ello, debemos añadir la librería de soporte para [Ktor Test](https://ktor.io/docs/test-client.html) y configurar sus
opciones.

Podemos usar la función testApplication para configurar una instancia configurada de nuestro servicio de prueba que se
ejecuta localmente. Con ello podemos usar la instancia de cliente HTTP de Ktor dentro de una aplicación de prueba para
realizar una solicitud a su servidor, recibir una respuesta y testear resultados.

```kotlin
fun registerUserTest() = testApplication {
    // Configuramos el entorno de test
    environment { config }

    val client = createClient {
        install(ContentNegotiation) {
            json()
        }
    }

    // Lanzamos la consulta
    val response = client.post("/api/users/register") {
        contentType(ContentType.Application.Json)
        setBody(userDto)
    }

    // Comprobamos que la respuesta y el contenido es correcto
    assertEquals(response.status, HttpStatusCode.Created)
    // Tambien podemos comprobar el contenido
    val res = json.decodeFromString<UserDto>(response.bodyAsText())
    assertAll(
        { assertEquals(res.nombre, userDto.nombre) },
        { assertEquals(res.email, userDto.email) },
        { assertEquals(res.username, userDto.username) },
        { assertEquals(res.avatar, userDto.avatar) },
        { assertEquals(res.role, userDto.role) },
    )
}
```

### Despliegue

Podemos distribuir nuestra app de distintas maneras

#### JAR

Podemos crear un JAR con nuestra aplicación y ejecutarla con el comando java -jar. Para ello, debemos añadir la librería
de soporte para [Ktor JAR](https://ktor.io/docs/jar.html) y configurar sus opciones en Gradle.

- buildFatJar: construye un JAR combinado de un proyecto y dependencias, como *-all.jar en el directorio build/libs
  cuando se complete esta compilación.
- runFatJar: construye un JAR del proyecto y lo ejecuta.

#### Aplicación

Podemos crear una aplicación y ejecutarla gracias a Gradle. Para ello, debemos añadir la librería de soporte
para [Ktor Application](https://ktor.io/docs/gradle-application-plugin.html#apply-plugin) y configurar sus opciones.

Esta opción nos proporciona varias formas de empaquetar la aplicación, por ejemplo, la tarea installDist instala la
aplicación con todas las dependencias de tiempo de ejecución y los scripts de inicio. Para crear archivos de
distribución completos.

#### Docker

Ktor tiene una [API de Docker](https://ktor.io/docs/docker.html) que nos permite crear una imagen de Docker con nuestra
aplicación.

- buildImage: construye la imagen de Docker de un proyecto en un tarball. Esta tarea genera un archivo jib-image.tar en
  el directorio de compilación. Puede cargar esta imagen en un demonio de Docker con el comando de carga de Docker:
  docker load < build/jib-image.tar
- publishImageToLocalRegistry: compila y publica la imagen de Docker de un proyecto en un registro local.
- runDocker: crea la imagen de un proyecto en un demonio Docker y lo ejecuta. Ejecutar esta tarea iniciará el servidor
  Ktor, respondiendo en http://0.0.0.0:8080 por defecto. Si su servidor está configurado para usar otro puerto, puede
  ajustar la asignación de puertos.
- publishImage: compila y publica la imagen de Docker de un proyecto en un registro externo, como Docker Hub o Google
  Container Registry. Tenga en cuenta que debe configurar el registro externo mediante la propiedad
  ktor.docker.externalRegistry para esta tarea.

```kotlin
ktor {
    docker {
        jreVersion.set(io.ktor.plugin.features.JreVersion.JRE_11)
        localImageName.set("sample-docker-image")
        imageTag.set("0.0.1-preview")
        portMappings.set(
            listOf(
                io.ktor.plugin.features.DockerPortMapping(
                    80,
                    8080,
                    io.ktor.plugin.features.DockerPortMappingProtocol.TCP
                )
            )
        )
    }
}
```

### Documentación

A la hora de documentar nuestro código hemos hecho uso de [Dokka](https://kotlinlang.org/docs/dokka-get-started.html) el
cual haciendo uso de [KDoc](https://kotlinlang.org/docs/dokka-get-started.html) nos va a permitir comentar nuestro
código y ver dicha documentación en html.
Puedes ver un ejemplo completo en todo lo relacionado con Representantes (modelos, repositorios y/o servicios) y
consultar la documentación en /build/dokka/html/index.html

Por otro lado se ha usado Swagger con OpenAPI para la documentación de la API. En vez de las librerías ofrecidas por el
equipo de Ktor ([OpenAPI](https://ktor.io/docs/openapi.html) y [Swagger](https://ktor.io/docs/swagger-ui.html)) hemos
usado [Ktor Swagger-UI](https://github.com/SMILEY4/ktor-swagger-ui) la cual extiende el DSL de Ktor para añadir la
documentación de Swagger-UI a nuestra aplicación sobre la marcha.

Puedes ver un ejemplo completo en todo lo relacionado con endpoint de Test (modelos, repositorios y/o servicios). Lo he
hecho así para no llenar el proyecto de código y ser un proyecto didáctico. Puedes consultar swagger
en: http://xxx/swagger/

```kotlin
// Put -> /{id}
put("{id}", {
    description = "Put By Id: Mensaje de prueba"
    request {
        pathParameter<String>("id") {
            description = "Id del mensaje de prueba"
            required = true // Opcional
        }
        body<TestDto> {
            description = "Mensaje de prueba de actualización"
        }
    }
    response {
        default {
            description = "Respuesta de prueba"
        }
        HttpStatusCode.OK to {
            description = "Mensaje de prueba modificado"
            body<TestDto> { description = "Mensaje de test modificado" }
        }

    }
}) {
    logger.debug { "PUT /test/{id}" }
    val id = call.parameters["id"]
    val input = call.receive<TestDto>()
    val dto = TestDto("TEST OK PUT $id : ${input.message}")
    call.respond(HttpStatusCode.OK, dto)
}
```

## Reactividad

Como todo concepto que aunque complicado de conseguir implica una serie de condiciones. La primera de ellas es asegurar
la asincronía en todo momento. Cosa que se ha hecho mediante Ktor y el uso de corrutinas.

Por otro lado el acceso de la base de datos no debe ser bloqueante, por lo que no se ha usado la librería Exposed de
Kotlin para acceder a la base de datos y que trabaja por debajo con el driver JDBC. Sabemos que esto se puede podemos
acercarnos a la Asincronía pura usando corrutinas y el manejo
de [contexto de transaccion asíncrono](https://github.com/JetBrains/Exposed/wiki/Transactions).

En cualquier caso, hemos decidido usar el driver R2DBC con el objetivo que el acceso a la base de datos sea no
bloqueante y así poder aprovechar el uso de Flows en Kotlin y así poder usar la reactividad total en la base de datos
con las corrutinas y Ktor.

![reactividad](./images/reactive.gif)


> **Programación reactiva: programación asíncrona de flujos observables**
>
> Programar reactivamente una api comienza desde observar y procesar las colecciones existentes de manera asíncrona
> desde la base de datos hasta la respuesta que se ofrezca.

## Inmutabilidad

Es importante que los datos sean inmutables, es decir, que no se puedan modificar una vez creados en todo el proceso de
las capas de nuestra arquitectura. Esto nos permite tener un código más seguro y predecible. En Kotlin, por defecto,
podemos hacer que una clase sea inmutable, añadiendo el modificador val a sus propiedades.

Para los POKOS (Plain Old Kotlin Objects) usaremos Data Classes, que son clases inmutables por defecto y crearemos
objetos nuevos con las modificaciones que necesitemos con la función copy().

## Caché

La [caché](https://es.wikipedia.org/wiki/Cach%C3%A9_(inform%C3%A1tica)) es una forma de almacenar datos en memoria/disco
para que se puedan recuperar rápidamente. Además de ser una forma de optimizar el rendimiento, también es una forma de
reducir el coste de almacenamiento de datos y tiempo de respuesta pues los datos se almacenan en memoria y no en disco o
base de datos que pueden estar en otro servidor y con ello aumentar el tiempo de respuesta.

Además la caché nos ofrece automáticamente distintos mecanismos de actuación, como por ejemplo, que los elementos en
cache tenga un tiempo de vida máximo y se eliminen automáticamente cuando se cumpla. Lo que nos permite tener datos
actualizados Y/o los más usados en memoria y eliminar los que no se usan.

En nuestro proyecto tenemos dos repositorios, uno para la caché y otro para la base de datos. Para ello todas las
consultas usamos la caché y si no está, se consulta a la base de datos y se guarda en la caché. Además, podemos tener un
proceso en background que actualice la caché cada cierto tiempo solo si así lo configuramos, de la misma manera que el
tiempo de refresco.

Además, hemos optimizado las operaciones con corrutinas para que se ejecuten en paralelo actualizando la caché y la base
de datos.

El diagrama seguido es el siguiente

![cache](./images/cache.jpg)

Por otro lado también podemos configurar la Caché de Header a nivel de rutas o tipo de ficheros como se ha indicado.

Para este proyecto hemos usado [Cache4K](https://reactivecircus.github.io/cache4k/). Cache4k proporciona un caché de
clave-valor en memoria simple para Kotlin Multiplatform, con soporte para ivalidar items basados ​​en el tiempo (
caducidad) y en el tamaño.

## Notificaciones en tiempo real

Las notificaciones en tiempo real son una forma de comunicación entre el servidor y el cliente que permite que el
servidor envíe información al cliente sin que el cliente tenga que solicitarla. Esto permite que el servidor pueda
enviar información al cliente cuando se produzca un evento sin que el cliente tenga que estar constantemente consultando
al servidor.

Para ello usaremos [WebSockets](https://developer.mozilla.org/es/docs/Web/API/WebSockets_API). A partir de aquí tenemos dos opciones que te he dejado en el código.

Aplicar el patrón [Observer](https://refactoring.guru/es/design-patterns/observer) para que el servidor pueda enviar información al cliente cuando se produzca un evento sin que el cliente tenga que estar constantemente consultando al servidor. Para ello, una vez el cliente se conecta al servidor, se le asigna un ID de sesión y se guarda en una lista de clientes
conectados. Cuando se produce un evento, se recorre la lista de clientes conectados y se envía la información a cada uno de ellos, ejecutando la función de callback que se le ha pasado al servidor. El patrón Observer es una buena opción cuando tienes una lista de suscriptores que necesita recibir notificaciones en tiempo real y deseas mantener un registro de los suscriptores activos. En este caso, cuando ocurre un cambio, deberás recorrer la lista de suscriptores y notificar a cada uno de ellos individualmente. Esto puede ser adecuado si la lista de suscriptores no es muy grande y no hay una gran cantidad de cambios que se produzcan con frecuencia. En esta solución te he dejado los Representantes.

Además, podemos hacer uso de las funciones de serialización para enviar objetos complejos como JSON.

![observer](./images/observer.png)

La siguiente opción es usar estados reactivos con [StateFlow](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-state-flow/) o con [SharedFlow](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-shared-flow/). El uso de StateFlow/SharedFlow en Kotlin puede ser beneficioso cuando deseas tener un flujo de datos reactivo. StateFlow/SharedFlow es una implementación de la interfaz Flow de Kotlin que te permite emitir cambios de estado de manera asincrónica. Puedes observar los cambios en el flujo y reaccionar solo cuando hay un cambio de estado relevante. Esto evita tener que recorrer manualmente una lista de suscriptores en cada cambio, ya que StateFlow/SharedFlow se encargará de notificar automáticamente a los observadores a través del websocket interesados cuando se produzca un cambio en el estado. Siguiendo esta forma te he dejado Tenistas y Raquetas.

![Flow](./images/flows.png)

La elección entre ambos enfoques depende de tus necesidades específicas. Si la lista de suscriptores es pequeña y los cambios ocurren con poca frecuencia, el patrón Observer puede ser una solución simple y adecuada. Sin embargo, si deseas aprovechar las capacidades reactivas de Kotlin y tener un flujo de datos que notifique automáticamente a los observadores cuando ocurra un cambio relevante, entonces StateFlow/SharedFlow podría ser una mejor opción.

## Proveedor de Dependencias

Gracias al principio de inversión de dependencias (SOLID), podemos hacer que el código que es el núcleo de nuestra
aplicación no dependa de los detalles de implementación, como pueden ser el framework que utilices, la base de datos,
cache...Todos estos aspectos se especificarán mediante interfaces, y el núcleo no tendrá que conocer cuál es la
implementación real para funcionar.

La Inyección de dependencias es un patrón de diseño que permite que las dependencias de una clase se pasen como
parámetros en el constructor de la clase (principalmente). Esto nos permite que las dependencias de una clase sean
independientes de la clase y que puedan ser reemplazadas por otras dependencias que implementen la misma interfaz y con
ello conseguir un código no acoplado, que se adapte a cada situación y que sea fácil de testear y con ello podemos
cumplir el principio de inversión de control.

Para ello usaremos [Koin](https://insert-koin.io/) que es un framework de inyección de dependencias para Kotlin
Multiplatform. Koin nos permite definir los módulos de inyección de dependencias y las dependencias que queremos
inyectar en cada clase. En este caso hemos usado sus extensiones
para [Ktor](https://insert-koin.io/docs/reference/koin-ktor/ktor) y
sus [anotaciones](https://insert-koin.io/docs/reference/koin-annotations/start) para hacerlo mucho más directo.

![koin](./images/koin.png)

## Railway Oriented Programming
[Railway Oriented Programming](https://fsharpforfunandprofit.com/rop/) es un patrón de diseño que nos permite escribir código más limpio y mantenible. Este patrón se basa en el concepto de [programación funcional](https://es.wikipedia.org/wiki/Programaci%C3%B3n_funcional) y en el uso de [monadas](https://es.wikipedia.org/wiki/Monada_(programaci%C3%B3n_funcional)). 

Es una técnica de programación funcional que nos permite manejar errores de forma más sencilla y segura. En lugar de usar excepciones, se usan valores de retorno o tipos de error para indicar si una operación ha tenido éxito o no. En el caso de que la operación haya fallado, se devuelve un valor que indica el error.

Se van encadenando operaciones que pueden fallar, y en caso de que alguna de ellas falle, se devuelve el error. De esta forma, se evita el uso de excepciones, que pueden ser difíciles de manejar. Tampoco tenemos que esperar que se ejecuten todas las operaciones para saber si ha fallado alguna, sino que en cuanto una operación falle, se devuelve el error.

![rop](./images/railway.png)

## Seguridad de las comunicaciones

### SSL/TLS

Para la seguridad de las comunicaciones
usaremos [SSL/TLS](https://es.wikipedia.org/wiki/Seguridad_de_la_capa_de_transporte) que es un protocolo de seguridad
que permite cifrar las comunicaciones entre el cliente y el servidor. Para ello usaremos un certificado SSL que nos
permitirá cifrar las comunicaciones entre el cliente y el servidor.

De esta manera, conseguiremos que los datos viajen cifrados entre el cliente y el servidor y que no puedan ser
interceptados por terceros de una manera sencilla.

Esto nos ayudará, a la hora de hacer el login de un usuario, a que la contraseña no pueda ser interceptada por terceros
y que el usuario pueda estar seguro de que sus datos están protegidos.

![tsl](./images/tsl.jpg)

### Autenticación y Autorización con JWT

Para la seguridad de las comunicaciones usaremos [JWT](https://jwt.io/) que es un estándar abierto (RFC 7519) que define
una forma compacta y autónoma de transmitir información entre partes como un objeto JSON. Esta información puede ser
verificada y confiada porque está firmada digitalmente. Las firmas también se pueden usar para asegurar la integridad de
los datos.

El funcionamiento de JWT es muy sencillo. El cliente hace una petición para autenticarse la primera vez. El servidor
genera un token que contiene la información del usuario y lo envía al cliente. El cliente lo guarda y lo envía en cada
petición al servidor. El servidor verifica el token y si es correcto, permite la petición al recurso.

![jwt](./images/tokens.png)

### CORS

Para la seguridad de las comunicaciones usaremos [CORS](https://developer.mozilla.org/es/docs/Web/HTTP/CORS) que es un
mecanismo que usa cabeceras HTTP adicionales para permitir que un user agent obtenga permiso para acceder a recursos
seleccionados desde un servidor, en un origen distinto (dominio) al que pertenece.

![cors](./images/cors.png)

### BCrypt

Para la seguridad de las comunicaciones usaremos [Bcrypt](https://en.wikipedia.org/wiki/Bcrypt) que es un algoritmo de
hash de contraseñas diseñado por Niels Provos y David Mazières, destinado a ser un método de protección contra ataques
de fuerza bruta. Con este algoritmo, se puede almacenar una contraseña en la base de datos de forma segura, ya que no se
puede obtener la contraseña original a partir de la contraseña almacenada.

![bcrypt](./images/bcrypt.png)

## Testing

Para testear se ha usado JUnit y MocKK como librerías de apoyo. Además, Hemos usado la propia api de Ktor para testear
las peticiones. Con ello podemos simular un Postman para testear las peticiones de manera local, con una instancia de
prueba de nuestro servicio.
![testear](./images/testing.png)

### Postman

Para probar con un cliente nuestro servicio usaremos [Postman](https://www.postman.com/) que es una herramienta de
colaboración para el desarrollo de APIs. Permite a los usuarios crear y compartir colecciones de peticiones HTTP, así
como documentar y probar sus APIs.

El fichero para probar nuestra api lo tienes en la carpera [postman](./postman) y puedes importarlo en tu Postman para
probar el resultado.

![postman](./images/postman.png)

## Distribución y Despliegue

Para la distribución de la aplicación usaremos [Docker](https://www.docker.com/) con su [Dockerfile](./Dockerfile).
Además, podemos usar [Docker Compose](https://docs.docker.com/compose/) con [docker-compose.yml](./docker-compose.yml)
que es una herramienta para definir y ejecutar aplicaciones Docker de múltiples contenedores.

![docker](./images/docker.jpg)

Por otro lado, podemos usar JAR o Aplicaciones de sistema tal y como hemos descrito en el apartado
de [Despliegue](#despliegue).

**Recuerda**: Si haces una imagen Docker mete todos los certificados y recursos que necesites que use adicionalmente
nuestra aplicación (directorios), si no no funcionará, pues así los usas en tu fichero de configuración. Recuerda lo que
usa tu fichero de [configuración](./src/main/kotlin/../resources/application.conf) para incluirlo.

## Documentación

La documentación sobre los métodos se pueden consultar en HTML realizada con Dokka.

La documentación de los endpoints se puede consultar en HTML realizada con Swagger.

![swagger](./images/swagger.png)

## Recursos

- Twitter: https://twitter.com/JoseLuisGS_
- GitHub: https://github.com/joseluisgs
- Web: https://joseluisgs.github.io
- Discord del módulo: https://discord.gg/RRGsXfFDya
- Aula DAMnificad@s: https://discord.gg/XT8G5rRySU

## Autor

Codificado con :sparkling_heart: por [José Luis González Sánchez](https://twitter.com/JoseLuisGS_)

[![Twitter](https://img.shields.io/twitter/follow/JoseLuisGS_?style=social)](https://twitter.com/JoseLuisGS_)
[![GitHub](https://img.shields.io/github/followers/joseluisgs?style=social)](https://github.com/joseluisgs)
[![GitHub](https://img.shields.io/github/stars/joseluisgs?style=social)](https://github.com/joseluisgs)

### Contacto

<p>
  Cualquier cosa que necesites házmelo saber por si puedo ayudarte 💬.
</p>
<p>
 <a href="https://joseluisgs.dev" target="_blank">
        <img src="https://joseluisgs.github.io/img/favicon.png" 
    height="30">
    </a>  &nbsp;&nbsp;
    <a href="https://github.com/joseluisgs" target="_blank">
        <img src="https://distreau.com/github.svg" 
    height="30">
    </a> &nbsp;&nbsp;
        <a href="https://twitter.com/JoseLuisGS_" target="_blank">
        <img src="https://i.imgur.com/U4Uiaef.png" 
    height="30">
    </a> &nbsp;&nbsp;
    <a href="https://www.linkedin.com/in/joseluisgonsan" target="_blank">
        <img src="https://upload.wikimedia.org/wikipedia/commons/thumb/c/ca/LinkedIn_logo_initials.png/768px-LinkedIn_logo_initials.png" 
    height="30">
    </a>  &nbsp;&nbsp;
    <a href="https://discordapp.com/users/joseluisgs#3560" target="_blank">
        <img src="https://logodownload.org/wp-content/uploads/2017/11/discord-logo-4-1.png" 
    height="30">
    </a> &nbsp;&nbsp;
    <a href="https://g.dev/joseluisgs" target="_blank">
        <img loading="lazy" src="https://googlediscovery.com/wp-content/uploads/google-developers.png" 
    height="30">
    </a>  &nbsp;&nbsp;
<a href="https://www.youtube.com/@joseluisgs" target="_blank">
        <img loading="lazy" src="https://upload.wikimedia.org/wikipedia/commons/e/ef/Youtube_logo.png" 
    height="30">
    </a>  
</p>

### ¿Un café?

<p><a href="https://www.buymeacoffee.com/joseluisgs"> <img align="left" src="https://cdn.buymeacoffee.com/buttons/v2/default-blue.png" height="50" alt="joseluisgs" /></a></p><br><br><br>

## Licencia de uso

Este repositorio y todo su contenido está licenciado bajo licencia **Creative Commons**, si desea saber más, vea
la [LICENSE](https://joseluisgs.dev/docs/license/). Por favor si compartes, usas o modificas este proyecto cita a su
autor, y usa las mismas condiciones para su uso docente, formativo o educativo y no comercial.

<a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/"><img alt="Licencia de Creative Commons" style="border-width:0" src="https://i.creativecommons.org/l/by-nc-sa/4.0/88x31.png" /></a><br /><span xmlns:dct="http://purl.org/dc/terms/" property="dct:title">
JoseLuisGS</span>
by <a xmlns:cc="http://creativecommons.org/ns#" href="https://joseluisgs.dev/" property="cc:attributionName" rel="cc:attributionURL">
José Luis González Sánchez</a> is licensed under
a <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/">Creative Commons
Reconocimiento-NoComercial-CompartirIgual 4.0 Internacional License</a>.<br />Creado a partir de la obra
en <a xmlns:dct="http://purl.org/dc/terms/" href="https://github.com/joseluisgs" rel="dct:source">https://github.com/joseluisgs</a>.
