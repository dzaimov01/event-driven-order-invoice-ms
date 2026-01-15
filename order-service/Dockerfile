FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml /workspace/pom.xml
COPY order-service/pom.xml /workspace/order-service/pom.xml
COPY invoice-service/pom.xml /workspace/invoice-service/pom.xml
COPY integration-tests/pom.xml /workspace/integration-tests/pom.xml
RUN mvn -q -B -pl order-service -am dependency:go-offline
COPY order-service /workspace/order-service
RUN mvn -q -B -pl order-service -am package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/order-service/target/order-service-*.jar /app/order-service.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/order-service.jar"]
