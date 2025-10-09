FROM openjdk:17

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} category-service.jar

ENTRYPOINT ["java", "-jar", "category-service.jar"]

EXPOSE 8091