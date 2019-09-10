FROM adoptopenjdk/openjdk11:latest
EXPOSE 9095
MAINTAINER care20@nl.abnamro.com
ADD /target/care-event-in-service-0.0.1-SNAPSHOT.jar care-event-in-service.jar

ENTRYPOINT ["java","-jar","care-event-in-service.jar"]