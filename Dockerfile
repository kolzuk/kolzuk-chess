FROM sbtscala/scala-sbt:eclipse-temurin-jammy-11.0.17_8_1.8.2_2.13.10

WORKDIR /app

COPY build.sbt ./
COPY project/ ./project/

COPY modules/ ./modules/

RUN sbt update
RUN sbt compile

RUN sbt chessEngine/assembly

EXPOSE 80

CMD ["java", "-jar", "modules/chess-engine/target/scala-2.13/chess-engine-assembly-0.1.0-SNAPSHOT.jar"]
