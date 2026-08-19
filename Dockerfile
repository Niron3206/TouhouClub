FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /build
# отдельным слоем, чтобы зависимости не перекачивались при каждой правке сурса
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B package

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /build/target/TouhouClub.jar .
CMD ["java", "-jar", "TouhouClub.jar"]
