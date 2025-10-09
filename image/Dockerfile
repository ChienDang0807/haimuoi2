FROM openjdk:17

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} image-service.jar

ENTRYPOINT ["java", "-jar", "image-service.jar"]

EXPOSE 8092