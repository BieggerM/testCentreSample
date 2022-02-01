FROM openjdk:12
ADD target/covidtestform-0.0.1-SNAPSHOT.jar covidtestform-0.0.1-SNAPSHOT.jar
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "covidtestform-0.0.1-SNAPSHOT.jar"]
