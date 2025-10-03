FROM openjdk:17

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} shop-service.jar

ENTRYPOINT ["java", "-jar", "shop-service.jar"]

EXPOSE 8091