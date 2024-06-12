#FROM maven:3.8.3-openjdk-17 AS builder
FROM maven:3.9.7-eclipse-temurin-17-alpine AS builder
COPY . /build/
WORKDIR /build/
RUN mvn clean package -DskipTests

FROM quay.io/wildfly/wildfly:latest-jdk17
RUN /opt/jboss/wildfly/bin/add-user.sh admin R3dh4t1! --silent
COPY --from=builder /build/target/cargo-tracker.war /opt/jboss/wildfly/standalone/deployments/ROOT.war

COPY wildfly-config.sh /opt/jboss/
COPY wildfly-embed-server.cli /opt/jboss/wildfly/bin/wildfly-embed-server.cli
COPY wildfly-scripts.cli /opt/jboss/wildfly-scripts.cli

RUN /opt/jboss/wildfly-config.sh

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0", "-c","standalone-full.xml"]
