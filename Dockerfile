FROM maven:3.6.0-jdk-11-slim
WORKDIR /usr/local/src/NCANode
RUN apt update && apt install -y crudini
COPY src src/
COPY lib lib/
COPY pom.xml ./
RUN mvn clean && mvn package
COPY NCANode.ini ./
RUN crudini --set NCANode.ini http ip 0.0.0.0

FROM openjdk:12-alpine
WORKDIR /opt/ncanode
CMD ["java", "-jar", "ncanode.jar"]
RUN mkdir logs \
 && ln -s /dev/stdout logs/request.log \
 && ln -s /dev/stderr logs/error.log \
 && mkdir -p ca/root \
 && cd ca/root && wget \
  http://www.pki.gov.kz/cert/pki_rsa.crt \
  http://www.pki.gov.kz/cert/pki_gost.crt \
  http://www.pki.gov.kz/cert/root_rsa.crt \
  http://www.pki.gov.kz/cert/root_gost.crt \
  http://www.pki.gov.kz/cert/nca_rsa.crt \
  http://www.pki.gov.kz/cert/nca_gost.crt
COPY --from=0 /usr/local/src/NCANode/NCANode.ini ./NCANode.ini
COPY --from=0 /usr/local/src/NCANode/target/ncanode-*-jar-with-dependencies.jar ./ncanode.jar
