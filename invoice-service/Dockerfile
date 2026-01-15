FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml /workspace/pom.xml
COPY order-service/pom.xml /workspace/order-service/pom.xml
COPY invoice-service/pom.xml /workspace/invoice-service/pom.xml
COPY integration-tests/pom.xml /workspace/integration-tests/pom.xml
RUN mvn -q -B -pl invoice-service -am dependency:go-offline
COPY invoice-service /workspace/invoice-service
RUN mvn -q -B -pl invoice-service -am package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/invoice-service/target/invoice-service-*.jar /app/invoice-service.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/invoice-service.jar"]
