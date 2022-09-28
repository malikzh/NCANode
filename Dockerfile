FROM amazoncorretto:17-alpine
EXPOSE 14579
WORKDIR /app
COPY build/libs/NCANode.jar /app
RUN mkdir /app/cache
VOLUME /app/cache
ENTRYPOINT ["java", "-jar", "NCANode.jar"]
HEALTHCHECK --interval=20s --timeout=30s \
    CMD wget -O - http://127.0.0.1:14579/actuator/health | grep -v DOWN || exit 1
