#Start with a base image containing Java runtime
FROM openjdk:17-jdk-slim

#Information around who maintains the image
MAINTAINER thietnguyen

WORKDIR /app

# Add the application's jar to the image
COPY target/users-0.0.1-SNAPSHOT.jar /app/

# execute the application
ENTRYPOINT ["java", "-jar", "users-0.0.1-SNAPSHOT.jar"]


#command
#mvn clean install
#docker build . -t nvthiet1995/users