FROM openjdk:17

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} payment-service.jar

ENTRYPOINT ["java", "-jar", "payment-service.jar"]

EXPOSE 8086