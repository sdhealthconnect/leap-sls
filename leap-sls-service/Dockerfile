FROM openjdk:11-jdk

ARG JAR_FILE=target/leap-sls-service-0.0.1.war
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Dspring.profiles.active=docker","-jar","/app.jar"]
