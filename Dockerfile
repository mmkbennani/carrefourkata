# cache as most as possible in this multistage dockerfile. 
FROM maven:3.9.6-sapmachine-21 as DEPS


COPY drive-and-deliver-domain/pom.xml /tmp/drive-and-deliver-domain/pom.xml
COPY drive-and-deliver-infra-kafka/pom.xml /tmp/drive-and-deliver-infra-kafka/pom.xml
COPY drive-and-deliver-application/pom.xml /tmp/drive-and-deliver-application/pom.xml
COPY drive-and-deliver-infra-h2/pom.xml /tmp/drive-and-deliver-infra-h2/pom.xml
COPY drive-and-deliver-exposition/pom.xml /tmp/drive-and-deliver-exposition/pom.xml
COPY pom.xml /tmp/

WORKDIR /tmp/

COPY  drive-and-deliver-domain/src /tmp/drive-and-deliver-domain/src
COPY  drive-and-deliver-infra-h2/src /tmp/drive-and-deliver-infra-h2/src
COPY  drive-and-deliver-infra-kafka/src /tmp/drive-and-deliver-infra-kafka/src
COPY  drive-and-deliver-application/src /tmp/drive-and-deliver-application/src
COPY  drive-and-deliver-exposition/src /tmp/drive-and-deliver-exposition/src

RUN mvn clean install -Pdocker

FROM maven:3.9.6-sapmachine-21 as BUILDER
WORKDIR /tmp/

COPY --from=DEPS /tmp/drive-and-deliver-exposition/target/drive-and-deliver-exposition.jar app.jar

EXPOSE 8080
RUN sh -c 'touch /app.jar'

ENTRYPOINT ["java","-jar","/app.jar"]
