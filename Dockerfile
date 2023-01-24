# Con este Dockerfile se crea una imagen de Docker que
# compila la aplicación gracias a Gradle
FROM gradle:7-jdk11 AS build
# Copiamos el codigo fuente de la aplicación, es decir, 
# lo que hay en el directorio actual
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

# Con esto hacemos una imagen de Docker que ejecuta la aplicación
FROM openjdk:11-jre-slim-buster
EXPOSE 6969:6969
EXPOSE 6963:6963
# Directorio donde se guarda la aplicación
RUN mkdir /app
# Copiamos los certificados y los recursos de la aplicación en los directorios que necesita
RUN mkdir /cert
COPY --from=build /home/gradle/src/cert/* /cert/
# Copiamos el JAR de la aplicación
COPY --from=build /home/gradle/src/build/libs/tenistas-rest-ktor-all.jar /app/tenistas-rest-ktor.jar
# Ejecutamos la aplicación, y le pasamos los argumentos si tiene
ENTRYPOINT ["java","-jar","/app/tenistas-rest-ktor.jar"]