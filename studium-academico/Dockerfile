# =========================
# STAGE 1 - BUILD
# =========================
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copia arquivos do Maven primeiro (cache)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

# Copia o restante do código
COPY src src

# Build do JAR
RUN ./mvnw clean package -DskipTests


# =========================
# STAGE 2 - RUN
# =========================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copia o JAR gerado
COPY --from=build /app/target/studium-academico-0.0.1-SNAPSHOT.jar app.jar

# Porta padrão (Render injeta PORT)
EXPOSE 8080

# Start da aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
