FROM openjdk:17

WORKDIR /app
COPY java/ .
RUN ./gradlew build

CMD ["./gradlew", "run"]
