#Start with a base image containing Java runtime
FROM openjdk:17-jdk-slim

#Information around who maintains the image
#MAINTAINER thietnguyen

# Add the application's jar to the image
COPY target/users-0.0.1-SNAPSHOT.jar users-0.0.1-SNAPSHOT.jar

# execute the application
ENTRYPOINT ["java", "-jar", "users-0.0.1-SNAPSHOT.jar"]


#command
#docker build . -t nvthiet1995/users:v1