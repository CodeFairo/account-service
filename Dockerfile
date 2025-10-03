# ================================
# STAGE 1: Build
# ================================
FROM maven:3.9.9-eclipse-temurin-21 AS builder

# Crear carpeta de trabajo
WORKDIR /app

# Copiar archivos pom.xml y descargar dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente
COPY src ./src

# Compilar y empaquetar (salta los tests para rapidez en CI/CD)
RUN mvn clean package -DskipTests

# ================================
# STAGE 2: Runtime
# ================================
FROM eclipse-temurin:21-jre

# Crear usuario no root por seguridad
RUN useradd -ms /bin/bash spring
USER spring

# Directorio de trabajo
WORKDIR /app

# Copiar jar desde el stage anterior
COPY --from=builder /app/target/account-service-0.0.1-SNAPSHOT.jar app.jar

# Puerto expuesto
EXPOSE 8081

# Variable de entorno (puedes sobreescribir en docker-compose/k8s)
ENV JAVA_OPTS=""

# Comando de inicio
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
